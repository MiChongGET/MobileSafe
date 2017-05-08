package cn.buildworld.com.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug;
import android.widget.TextView;

/**
 * 作者：MiChong on 2017/4/5 0005 15:49
 * 邮箱：1564666023@qq.com
 * 自定义TextView
 */
public class FocusedTextView extends TextView {
    public FocusedTextView(Context context) {
        super(context);
    }

    public FocusedTextView(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 当前并没有焦点
     * @return
     */
    @Override
    @ViewDebug.ExportedProperty(category = "focus")
    public boolean isFocused() {
        return true;
    }
}
