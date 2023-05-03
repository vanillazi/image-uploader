package cn.vanillazi.tool;

import android.content.Context;
import android.net.Uri;

import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.utils.DateUtils;

import java.io.File;
import java.util.ArrayList;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnNewCompressListener;

public class DefaultCompressFileEngine implements CompressFileEngine {
    @Override
    public void onStartCompress(Context context, ArrayList<Uri> source, OnKeyValueResultCallbackListener call) {
        Luban.with(context).load(source).ignoreBy(100)
                .setRenameListener(filePath -> {
                    var indexOf = filePath.lastIndexOf(".");
                    var postfix =  indexOf != -1?filePath.substring(indexOf):".jpg";
                    return DateUtils.getCreateFileName("CMP_") + postfix;
                })
                .setCompressListener(new OnNewCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(String index, File compressFile) {
                        if (call != null) {
                            call.onCallback(index, compressFile.getAbsolutePath());
                        }
                    }

                    @Override
                    public void onError(String index, Throwable e) {
                        if (call != null) {
                            call.onCallback(index, null);
                        }
                    }
                })
                .launch();
    }
}
