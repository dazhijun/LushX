package cn.mailu.LushX.crawler;


import cn.mailu.LushX.constant.VideoTypeEnum;
import cn.mailu.LushX.entity.Video;
import cn.mailu.LushX.util.JsoupUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component

public class VideosCrawler {

    private static Logger logger= LoggerFactory.getLogger(VideosCrawler.class);
    private final static String YK_TV_URL = "http://list.youku.com/category/show/c_97.html";
    private final static String YK_MOVIE_URL = "http://list.youku.com/category/show/c_96.html";
    private final static String YK_ZY_URL = "http://list.youku.com/category/show/c_85.html";
    private final static String YK_DM_URL = "http://list.youku.com/category/show/c_100.html";

    private final static String IQY_TV_URL = "http://list.iqiyi.com/www/2/----------------iqiyi--.html";
    private final static String IQY_MOVIE_URL = "http://list.iqiyi.com/www/1/----------------iqiyi--.html";
    private final static String IQY_ZY_URL = "http://list.iqiyi.com/www/6/----------------iqiyi--.html";
    private final static String IQY_DM_URL = "http://list.iqiyi.com/www/4/----------------iqiyi--.html";

    private final RedisSourceManager redisSourceManager;

    public VideosCrawler(RedisSourceManager redisSourceManager) {
        this.redisSourceManager = redisSourceManager;
    }

    /**
     * 每隔1小时，爬乐视官网信息
     */
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void start() {
        // 爬取优酷排行榜视频
        Document ykTv = JsoupUtils.getDocWithPC(YK_TV_URL);
        Document ykMovie = JsoupUtils.getDocWithPC(YK_MOVIE_URL);
        Document ykZy = JsoupUtils.getDocWithPC(YK_ZY_URL);
        Document ykDm = JsoupUtils.getDocWithPC(YK_DM_URL);
        saveVideosToRedis(ykTv, VideoTypeEnum.YK_TV.getCode());
        saveVideosToRedis(ykMovie, VideoTypeEnum.YK_MOVIE.getCode());
        saveVideosToRedis(ykZy, VideoTypeEnum.YK_ZY.getCode());
        saveVideosToRedis(ykDm, VideoTypeEnum.YK_DM.getCode());

        // 爬取爱奇艺排行榜视频
        Document iqyTv = JsoupUtils.getDocWithPC(IQY_TV_URL);
        Document iqyMovie = JsoupUtils.getDocWithPC(IQY_MOVIE_URL);
        Document iqyZy = JsoupUtils.getDocWithPC(IQY_ZY_URL);
        Document iqyDm = JsoupUtils.getDocWithPC(IQY_DM_URL);
        saveVideosToRedis(iqyTv, VideoTypeEnum.IQY_TV.getCode());
        saveVideosToRedis(iqyMovie, VideoTypeEnum.IQY_MOVIE.getCode());
        saveVideosToRedis(iqyZy, VideoTypeEnum.IQY_ZY.getCode());
        saveVideosToRedis(iqyDm, VideoTypeEnum.IQY_DM.getCode());

    }

    private void saveVideosToRedis(Document document, int videoType) {
        String key = redisSourceManager.VIDEOS_KEY + "_" + videoType;
        int sourceType = videoType / 10;
        logger.info("资源类型 ：" + sourceType);
        if (sourceType == 0){
            redisSourceManager.saveVideos(key, getYKVideosFromPcDocument(document));
        }else if (sourceType == 1){
            redisSourceManager.saveVideos(key, getIQYVideosFromPcDocument(document));
        }
    }

    private List<Video> getYKVideosFromPcDocument(Document document) {
        List<Video> videos = new ArrayList<>();
        Elements videoElements = document.select("li.yk-col4");
        for (Element element : videoElements) {
            Video video = new Video();
            String title = element.select("div.p-thumb a").get(0).attr("title");
            String image = element.select("img.quic").attr("src").replace("http:", "");
            String url = "http:" + element.select("div.p-thumb a").get(0).attr("href");
            video.setTitle(title);
            video.setImage(image);
            video.setValue(url);
            logger.info("YK:" + title);
            videos.add(video);
        }
        return videos;
    }

    private List<Video> getIQYVideosFromPcDocument(Document document) {
        List<Video> videos = new ArrayList<>();
        Elements videoElements = document.select("ul.site-piclist-auto li");
        for (Element element : videoElements) {
            Video video = new Video();
            String url = element.select("p.site-piclist_info_title a").attr("href");
            String title = element.select("p.site-piclist_info_title a").text();
            String image = element.select("div.site-piclist_pic img").attr("src");
            video.setTitle(title);
            video.setImage(image);
            video.setValue(url);
            logger.info("IQY:" + title);
            videos.add(video);
        }
        return videos;
    }

}