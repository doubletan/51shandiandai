package org.xqj.shandiandai.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by apple on 2017/4/11.
 */

public class ImagerBean implements Serializable {

    private List<DaohangBean> Daohang;

    public List<DaohangBean> getDaohang() {
        return Daohang;
    }

    public void setDaohang(List<DaohangBean> Daohang) {
        this.Daohang = Daohang;
    }

    public static class DaohangBean implements Serializable {
        /**
         * link : http://ios.wecash.net/wep/simple_h5.html?version=h5&channelId=863&channelCode=11219a
         * advpath : /SysManage/UploadFile/Adv/20160922184234986898122.jpg
         * orderpm : 1
         */

        private String link;
        private String advpath;
        private String orderpm;

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getAdvpath() {
            return advpath;
        }

        public void setAdvpath(String advpath) {
            this.advpath = advpath;
        }

        public String getOrderpm() {
            return orderpm;
        }

        public void setOrderpm(String orderpm) {
            this.orderpm = orderpm;
        }

        @Override
        public String toString() {
            return "DaohangBean{" +
                    "link='" + link + '\'' +
                    ", advpath='" + advpath + '\'' +
                    ", orderpm='" + orderpm + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ImagerBean{" +
                "Daohang=" + Daohang +
                '}';
    }
}
