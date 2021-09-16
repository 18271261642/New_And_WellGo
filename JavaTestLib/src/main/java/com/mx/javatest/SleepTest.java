package com.mx.javatest;

/**
 * 作者:东芝(2019/3/25).
 * 功能:
 */

public class SleepTest {
    public static void main(String[] args) {

//        // 将用户信息用元组输入（年龄，睡眠时长，入睡时间，深睡占比，清醒次数）
//        // y = (25, 8 * 60, 22 * 60, 20, 1)
//        //睡眠得分=清醒次数+睡眠时长得分+入睡时间得分+深睡占比得分）
//        int wakeUpCount = 1;
//        int age = 25;
//        int sleepTotalTime = 8 * 60/*12 * 60*/;
//        int sleepStartTime = 22 * 60/*22 * 60*/;
//        int sleepPercent = 20;
//
//        float score = sleepScore(wakeUpCount, age, sleepTotalTime, sleepStartTime, sleepPercent);
//
//
//        //依据总得分获取睡眠断言
//        pingyu(score);

        int deepTotal = 2 * 60;
        int lightTotal = 6 * 60;
        int soberTotal = 1;

        //总睡眠时间 (分)
        int sleepTimeTotal = deepTotal + lightTotal + soberTotal;
        //深睡占比 %
        float percent = ((deepTotal) * 1.0f / sleepTimeTotal) * 100;

        int score;//分数,  4321 分别为: 优秀 良好 一般 较差
        if (sleepTimeTotal >= 12 * 60) {//睡超过12小时,不管深睡睡占比多少,一律视为 一般,  即使睡24小时 也不会评较差
            score = 2;
        } else if(sleepTimeTotal >= 10 * 60) {//睡10-11小时,不管深睡睡占比多少,一律视为良好
            score = 3;
        } else if (sleepTimeTotal >= 7 * 60) { //睡7-9小时 深睡占比30%以上为优秀
            if (percent > 30) {
                score = 4;
            } else {
                score = 3;
            }
        } else if (sleepTimeTotal >= 5 * 60) {//睡5-6小时 为良好
            score = 3;
        } else if (sleepTimeTotal >= 3 * 60) { //睡3-4小时 深睡占比低于50%以上为差,否则为一般
            if (percent > 50) {
                score = 2;
            } else {
                score = 1;
            }
        } else { //睡眠低于3小时  不管深睡睡占比多少, 一律视为较差
            score = 1;
        }


        switch (score) {
            case 4:
                System.out.println("优秀");
                break;
            case 3:
                System.out.println("良好");
                break;
            case 2:
                System.out.println("一般");
                break;
            case 1:
                System.out.println("较差");
                break;
        }
    }

    private static float sleepScore(int wakeUpCount, int age, int sleepTotalTime, int sleepStartTime, int sleepPercent) {
        //计算清醒次数得分
        float score = wakeUpCount;
        //依年龄计算判断数据
        float[][] table = init(age);
        //计算清醒次数和睡眠时长总得分
        score = score_data(score, sleepTotalTime, table[0][0], table[0][1], table[0][2], table[0][3]);
        //计算清醒次数，睡眠时长和入睡时间总得分
        score = score_time(score, sleepStartTime, table[1][0], table[1][1], table[1][2], table[1][3]);
        //计算清醒次数，睡眠时长，入睡时间和深睡占比总得分
        score = score_deep(score, sleepPercent);
        return score;
    }


    //通过年龄判断睡眠时长和入睡时间的判定值，（睡眠时长判断值，入睡时间判断值）
    //睡眠时长判断值=（最佳睡眠时长最大值，最佳睡眠时长最小值，推荐最少睡眠时长，睡眠时长最低阈值）
    //入睡时间判断值=（最佳入睡时间最小值，最佳入睡时间最大值，推荐最迟入睡时间，入睡时间阈值））
    static float[][] init(int y) {
        if (y <= 12) {
            return new float[][]{
                    {12 * 60 + 5, 9 * 60 - 5, 8 * 60 - 5, 7 * 60 - 5},
                    {21 * 60 - 5, 21 * 60 + 30 + 5, 21 * 60 + 90 + 5, 21 * 60 + 150 + 5}
            };
        } else if (y <= 18 && y > 12) {
            return new float[][]{
                    {10 * 60 + 5, 8 * 60 - 5, 7 * 60 - 5.6f * 60 - 5},
                    {22 * 60 - 5, 22 * 60 + 30 + 5, 22 * 60 + 90 + 5, 22 * 60 + 150 + 5}
            };
        } else if (y <= 60 && y > 18) {
            return new float[][]{
                    {8 * 60 + 5, 7 * 60 - 5, 6 * 60 - 5, 5 * 60 - 5},
                    {22 * 60 + 30 - 5, 22 * 60 + 30 + 30 + 5, 22 * 60 + 30 + 90 + 5, 22 * 60 + 30 + 150 + 5}
            };
        } else/* if (y > 60)*/ {
            return new float[][]{
                    {7 * 60 + 5, 6 * 60 - 5, 5 * 60 - 5, 4 * 60 - 5},
                    {22 * 60 - 5, 22 * 60 + 30 + 5, 22 * 60 + 90 + 5, 22 * 60 + 150 + 5}
            };
        }
    }

    //判断睡眠时长得分，c为分数，data为实际睡眠时长，abde睡眠时长判断值
    static float score_data(float score, float data, float a, float b, float d, float e) {
        if (data >= a + 2 * 60) {
            score += 3;
        } else if (data < a + 2 * 60 && data >= a) {
            score += 1;
        } else if (data < a && data >= b) {
            //pass
        } else if (data < b && data >= d) {
            score += 1;
        } else if (data < d && data >= e) {
            score += 2;
        } else if (data < e && data >= 4 * 60) {
            score += 3;
        } else if (data < 4 * 60) {
            score += 8;
        }
        return score;
    }

    static float time(float time) {
        if (time <= 21 * 60) {
            time += 1440;
        }
        return time;
    }

    //判断睡眠时长得分，c为分数，time为实际睡眠时长，abde入睡时间判断值
    //当入睡时间跨天时，将时钟数+24，如：凌晨1点睡，计算时，time值当25判断
    static float score_time(float score, float time, float a, float b, float d, float e) {
        time = time(time);
        if (time <= a) {
            score += 1;
        } else if (time > a && time <= b) {
            //pass
        } else if (time > b && time <= d) {
            score += 1;
        } else if (time > d && time <= e) {
            score += 2;
        } else if (time > e && time <= a + 4 * 60) {
            score += 3;
        } else if (time > a + 4 * 60) {
            score += 6;
        }
        return score;
    }


    //判断睡眠时长得分，c为分数，deep为深睡占比
    static float score_deep(float score, float deep) {
        if (deep > 40) {
            score += 1;
        } else if (deep <= 40 && deep > 20) {
            score += 0;
        } else if (deep <= 20 && deep > 15) {
            score += 1;
        } else if (deep <= 15 && deep > 5) {
            score += 2;
        } else if (deep <= 5) {
            score += 8;
        }
        return score;
    }

    //判断睡眠得分
    static void pingyu(float score) {
        if (score <= 3) {
            System.out.println("优秀");
        } else if (score <= 5 && score > 3) {
            System.out.println("良好");
        } else if (score <= 7 && score > 5) {
            System.out.println("一般");
        } else if (score > 7) {
            System.out.println("欠佳");
        }
    }
}