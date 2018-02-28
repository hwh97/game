package com.gdlinkjob.game.api;

/**
 *
 * @author hwhong
 * @创建日期 2018/1/29/ 0029 上午 10:07
 */
public class ApiUtils {
    private static ApiServices gameApi;

    public static ApiServices getGameApi(String url) {
        if (gameApi == null) {
            gameApi = RetrofitUtils.get(url).retrofit().create(ApiServices.class);
        }
        return gameApi;
    }
}
