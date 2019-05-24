package com.roll.localdriver.app;

import android.app.Application;

import com.roll.localdriverlibrary.main.LocalDriverMain;

/**
 * @date: on 2019/5/24
 * @author: a112233
 * @email: mxnzp_life@163.com
 * @desc: 添加描述
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LocalDriverMain.init(this, "aHR0cDovLzE5Mi4xNjguNC4yMTE6ODA4MD1jYzcyZTBjYTRlM2M0MTZjODU5ZmM4Zjg1NzA5OTBhMw==");
    }
}
