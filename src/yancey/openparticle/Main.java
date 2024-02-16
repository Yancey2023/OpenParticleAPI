package yancey.openparticle;

import yancey.openparticle.api.activity.ActivityManager;
import yancey.openparticle.api.common.OpenParticleAPI;
import yancey.openparticle.api.util.MyLogger;
import yancey.openparticle.test.ActivityTest2;

public class Main {

    public static void main(String[] args) {
        OpenParticleAPI openParticleAPI = new OpenParticleAPI(identifier -> 0, new MyLogger());
//        ActivityManager.add(ActivityTest.class);
        ActivityManager.add(ActivityTest2.class);
        ActivityManager.output(openParticleAPI);
//        ActivityManager.outputAndRun(openParticleAPI, false);

//        File file = new File("D:\\PyCharm\\project\\openparticle\\particles.par");
//        DataParticleManager input = openParticleAPI.input(file);
    }

}
