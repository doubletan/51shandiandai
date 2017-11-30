package org.xqj.shandiandai.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by apple on 2017/4/11.
 */

public class Product implements Serializable {

    private List<PrdListBean> prdList;

    public List<PrdListBean> getPrdList() {
        return prdList;
    }

    public void setPrdList(List<PrdListBean> prdList) {
        this.prdList = prdList;
    }

    public static class PrdListBean implements Serializable {
        /**
         * name : 手机贷
         * uid : 16
         * logo : /SysManage/UploadFile/wd/20160614150101108885567.png
         * orderpm : 1
         * link : http://m.mobp2p.com/?channel=bjxinhe01-llcs
         * summary : 费用超级低，要借钱快点来
         * lines : 1000-3000元
         * timeLimit : 21天
         * rate : 0.3%/天
         * speed : 24小时
         * difficulty : 9%左右
         * demand1 : 手机实名认证
         * demand2 : 电商认证，淘宝或京东都可以
         * tips1 : 21天借款时间特别适合工薪族的用款需求
         * tips2 : 客服电话：021-80203636
         */

        private String name;
        private String uid;
        private String logo;
        private String orderpm;
        private String link;
        private String summary;
        private String lines;
        private String timeLimit;
        private String rate;
        private String speed;
        private String difficulty;
        private String demand1;
        private String demand2;
        private String tips1;
        private String tips2;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getOrderpm() {
            return orderpm;
        }

        public void setOrderpm(String orderpm) {
            this.orderpm = orderpm;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getLines() {
            return lines;
        }

        public void setLines(String lines) {
            this.lines = lines;
        }

        public String getTimeLimit() {
            return timeLimit;
        }

        public void setTimeLimit(String timeLimit) {
            this.timeLimit = timeLimit;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public String getSpeed() {
            return speed;
        }

        public void setSpeed(String speed) {
            this.speed = speed;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(String difficulty) {
            this.difficulty = difficulty;
        }

        public String getDemand1() {
            return demand1;
        }

        public void setDemand1(String demand1) {
            this.demand1 = demand1;
        }

        public String getDemand2() {
            return demand2;
        }

        public void setDemand2(String demand2) {
            this.demand2 = demand2;
        }

        public String getTips1() {
            return tips1;
        }

        public void setTips1(String tips1) {
            this.tips1 = tips1;
        }

        public String getTips2() {
            return tips2;
        }

        public void setTips2(String tips2) {
            this.tips2 = tips2;
        }

        @Override
        public String toString() {
            return "PrdListBean{" +
                    "name='" + name + '\'' +
                    ", uid='" + uid + '\'' +
                    ", logo='" + logo + '\'' +
                    ", orderpm='" + orderpm + '\'' +
                    ", link='" + link + '\'' +
                    ", summary='" + summary + '\'' +
                    ", lines='" + lines + '\'' +
                    ", timeLimit='" + timeLimit + '\'' +
                    ", rate='" + rate + '\'' +
                    ", speed='" + speed + '\'' +
                    ", difficulty='" + difficulty + '\'' +
                    ", demand1='" + demand1 + '\'' +
                    ", demand2='" + demand2 + '\'' +
                    ", tips1='" + tips1 + '\'' +
                    ", tips2='" + tips2 + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "prdList=" + prdList +
                '}';
    }
}
