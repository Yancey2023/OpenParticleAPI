package yancey.openparticle.test;

import yancey.openparticle.api.activity.Activity;
import yancey.openparticle.api.common.data.vec3.DataVec3Free;
import yancey.openparticle.api.common.math.Matrix;
import yancey.openparticle.api.common.math.Vec3;
import yancey.openparticle.api.particle.Particle;
import yancey.openparticle.api.type.ParticleType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ActivityTest extends Activity {


    @Override
    protected File getFile() {
        return new File("D:\\IDEA\\project\\others\\OpenParticleAPI\\run");
    }

    @Override
    protected Vec3 getPosition() {
        return new Vec3(0, -58, 0);
    }

    @Override
    protected void createParticle() {
        butterfly(80, 0.2F);
    }

    public void butterfly(int age, float rotateChangeRange) {
        addParticle(ParticleType.END_ROD
                .createParticle(age)
                //生成蝴蝶
                .compound(particle -> {
                    List<Particle> particleList = new ArrayList<>();
                    double r = 1;
                    for (double theta = 0; theta < 2 * Math.PI; theta += 0.02) {
                        float r0 = (float) (2 * Math.abs(Math.sin(2 * theta)) + Math.abs(Math.sin(4 * theta)));
                        float x = (float) (r * r0 * Math.sin(theta));
                        float y = (float) (r * r0 * Math.cos(theta));
                        Vec3[] positions = new Vec3[age];
                        for (int i = 0; i < age; i++) {
                            positions[i] = new Vec3(x, (float) (Math.abs(y) * Math.sin(0.5 * i)), y);
                        }
                        particleList.add(particle
                                .offsetStatic(x, 0, y)
                                .offset(new DataVec3Free(positions))
                        );
                    }
                    return particleList;
                })
                .useCache()
                //倾斜45度
                .rotate(0, 0, (float) Math.PI / 4)
                .useCache()
                .apply(particle -> {
                    //获得时间
                    //随机生成每个时刻旋转的角度，获得旋转矩阵
                    Vec3 rotate = getRandomVec3((float) Math.PI);
                    Vec3 rotateChange = Vec3.ZERO;
                    Vec3[] rotateList = new Vec3[age];
                    Matrix[] rotateMatrixList = new Matrix[age];
                    for (int i = 0; i < age; i++) {
                        rotateList[i] = rotate;
                        rotateMatrixList[i] = Matrix.rotateXYZ(rotate);
                        if (i % 10 == 0) {
                            rotateChange = getRandomVec3(rotateChangeRange);
                        }
                        rotate = rotate.add(rotateChange);
                    }
                    //算出每个时刻的位移
                    Vec3 offset = Vec3.ZERO;
                    Vec3[] offsetList = new Vec3[age];
                    for (int i = 0; i < age; i++) {
                        offsetList[i] = offset;
                        offset = offset.add(rotateMatrixList[i].apply(new Vec3(0.5F, 0, 0)));
                    }
                    return particle
                            .rotate(new DataVec3Free(rotateList))
                            .offset(new DataVec3Free(offsetList));
                })
                .repeat(100)
                .offsetStatic(0, 10, 0)
        );
    }
}