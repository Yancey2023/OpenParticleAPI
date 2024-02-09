package yancey.openparticle.api.activity;

import yancey.openparticle.api.common.OpenParticleAPI;
import yancey.openparticle.api.common.controller.SimpleParticleController;
import yancey.openparticle.api.common.data.DataParticleManager;
import yancey.openparticle.api.common.data.DataRunningPerTick;
import yancey.openparticle.api.common.data.ParticleState;
import yancey.openparticle.api.common.data.identifier.IdentifierCache;
import yancey.openparticle.api.common.math.Vec3;
import yancey.openparticle.api.particle.Particle;
import yancey.openparticle.api.util.version.Version;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public abstract class Activity {

    protected final Random random = new Random(getRandomSeed());
    private final List<Particle> particleList = new ArrayList<>();

    /**
     * 随机值种子
     */
    protected long getRandomSeed() {
        return 0;
    }

    protected Version getVersion() {
        return Version.Java1_16;
    }

    protected abstract File getFile();

    protected abstract Vec3 getPosition();

    protected boolean isRemoveBlameTick() {
        return false;
    }

    /**
     * 创造粒子，这是你需要重写的方法
     */
    protected void addParticle(Particle particle) {
        particleList.add(particle);
    }

    protected abstract void createParticle();

    protected Vec3 getRandomVec3(float range) {
        return new Vec3(
                random.nextFloat(-range, range),
                random.nextFloat(-range, range),
                random.nextFloat(-range, range)
        );
    }

    public void output(OpenParticleAPI openParticleAPI, boolean isRun, boolean isSaveFile) {
        System.out.println("----- " + getClass().getSimpleName() + " -----");
        System.out.println("开始阶段1: 获取粒子数据");
        long timeStart1 = System.currentTimeMillis();
        createParticle();
        if (particleList.isEmpty()) {
            openParticleAPI.logger.warn("粒子为空，你是不是忘记调用addParticle()");
            return;
        }
        Particle particle = Particle.compound(particleList).offsetStatic(getPosition());
        long timeEnd1 = System.currentTimeMillis();
        System.out.printf("阶段1: 运行耗时%dms\n\n", timeEnd1 - timeStart1);

        System.out.println("开始阶段2: 计算粒子数据");
        long timeStart2 = System.currentTimeMillis();
        IdentifierCache.identifierList.clear();
        DataParticleManager dataParticleManager = new DataParticleManager();
        dataParticleManager.add(particle.execute0(getVersion()));
        long timeEnd2 = System.currentTimeMillis();
        System.out.printf("阶段2: 运行耗时%dms\n", timeEnd2 - timeStart2);
        long count = dataParticleManager.getParticleCount();
        System.out.printf("阶段2: 即将写入%d个粒子\n\n", count);

        System.out.println("开始阶段3: 写入粒子数据");
        File file = new File(getFile(), "particle.par");
        long timeStart3 = System.currentTimeMillis();
        openParticleAPI.output(file, dataParticleManager);
        long timeEnd3 = System.currentTimeMillis();
        System.out.printf("阶段3: 运行耗时%dms\n", timeEnd3 - timeStart3);
        long fileSize = file.length();
        System.out.printf("阶段3: 文件大小为%s\n\n", formatFileSize(fileSize));

        long timeStart4 = 0, timeEnd4 = 0, timeStart5 = 0, timeEnd5 = 0;
        if (isRun) {
            System.out.println("开始阶段4: 读取粒子数据");
            timeStart4 = System.currentTimeMillis();
            DataParticleManager input = openParticleAPI.input(file);
            timeEnd4 = System.currentTimeMillis();
            System.out.printf("阶段4: 运行耗时%dms\n\n", timeEnd4 - timeStart4);

            System.out.println("开始阶段5: 运行粒子数据");
            timeStart5 = System.currentTimeMillis();
            DataRunningPerTick[] dataRunningList = input.getDataRunningList();
            if (isSaveFile) {
                String string = Arrays.stream(dataRunningList)
                        .flatMap(dataRunningPerTick -> dataRunningPerTick.controllerList.stream()
                                .map(controller -> {
                                    List<ParticleState> particleStateList = new ArrayList<>();
                                    int age = controller.getAge();
                                    for (int i = 0; i < age; i++) {
                                        particleStateList.add(controller.getParticleState(i));
                                    }
                                    List<String> stringList2 = new ArrayList<>();
                                    for (ParticleState particleState : particleStateList) {
                                        stringList2.add(particleState.toString());
                                    }
                                    String first = stringList2.get(0);
                                    for (String s : stringList2) {
                                        if (!Objects.equals(first, s)) {
                                            return String.join("\n", stringList2);
                                        }
                                    }
                                    return first + " | ×" + stringList2.size();
                                })
                        ).collect(Collectors.joining("\n\n"));
                try {
                    Files.writeString(getFile().toPath().resolve("run.txt"), string);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                ExecutorService executorService = Executors.newFixedThreadPool(16);
                List<CompletableFuture<Void>> futureList = new ArrayList<>();
                for (DataRunningPerTick dataRunningPerTick : dataRunningList) {
                    for (SimpleParticleController controller : dataRunningPerTick.controllerList) {
                        futureList.add(CompletableFuture.runAsync(() -> {
                            int age = controller.getAge();
                            for (int i = 0; i < age; i++) {
                                controller.getParticleState(i);
                            }
                        }));
                    }
                }
                for (CompletableFuture<Void> future : futureList) {
                    future.join();
                }
            }
            timeEnd5 = System.currentTimeMillis();
            System.out.printf("阶段5: 运行耗时%dms\n\n", timeEnd5 - timeStart5);
        }

        System.out.printf("导出耗时: %dms\n", timeEnd3 - timeStart1);
        if (isRun) {
            System.out.printf("导入耗时: %dms\n", timeEnd4 - timeStart4);
            System.out.printf("运行耗时: %dms\n", timeEnd5 - timeStart5);
        }
        System.out.printf("粒子数量: %d\n", count);
        System.out.printf("文件大小: %s\n\n", formatFileSize(fileSize));
    }

    private String formatFileSize(long size) {
        if (size <= 1024) {
            return String.format("%dB", size);
        }
        double temp = size;
        temp /= 1024;
        if (temp <= 1024) {
            return String.format("%.2fKB", temp);
        }
        temp /= 1024;
        if (temp <= 1024) {
            return String.format("%.2fMB", temp);
        }
        temp /= 1024;
        return String.format("%.2fGB", temp);
    }

}