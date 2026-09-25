package com.saanvi.daily;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
import android.view.animation.BounceInterpolator;

public class CoinJarView extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private double balance=0;
    private int accent=Color.rgb(114,88,232);

    public CoinJarView(Context c){super(c);setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
    public void setAccent(int c){accent=c;invalidate();}
    public void setBalance(double b){balance=b;invalidate();}
    public void animateTo(double target){
        double from=balance;
        ValueAnimator a=ValueAnimator.ofFloat((float)from,(float)target);
        a.setDuration(900);a.setInterpolator(new BounceInterpolator());
        a.addUpdateListener(v->{balance=(Float)v.getAnimatedValue();invalidate();});
        a.start();
    }

    @Override protected void onDraw(Canvas c){
        super.onDraw(c);
        float w=getWidth(),h=getHeight();
        float l=w*.15f,r=w*.85f,t=h*.16f,b=h*.90f;
        p.setShadowLayer(w*.035f,0,w*.02f,Color.argb(90,accent>>16&255,accent>>8&255,accent&255));
        p.setStyle(Paint.Style.FILL);p.setColor(Color.argb(48,accent>>16&255,accent>>8&255,accent&255));
        Path jar=new Path();jar.moveTo(l,t);jar.lineTo(r,t);jar.lineTo(r*.96f,b);jar.quadTo(w*.5f,h*.98f,l*1.04f,b);jar.close();c.drawPath(jar,p);
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(Math.max(3,w*.025f));p.setColor(accent);c.drawPath(jar,p);p.clearShadowLayer();
        p.setStyle(Paint.Style.FILL);p.setColor(accent);c.drawRoundRect(w*.25f,h*.07f,w*.75f,h*.19f,w*.06f,w*.06f,p);

        int count=(int)Math.min(18,Math.max(0,Math.round(balance/10.0)));
        p.setColor(Color.rgb(251,191,36));
        for(int i=0;i<count;i++){
            int col=i%4,row=i/4;
            float x=l+w*.12f+col*w*.15f;
            float y=b-h*.08f-row*h*.09f-(col%2)*h*.012f;
            c.drawCircle(x,y,w*.055f,p);
            p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2);p.setColor(Color.rgb(205,135,32));c.drawCircle(x,y,w*.055f,p);
            p.setStyle(Paint.Style.FILL);p.setColor(Color.rgb(245,176,65));
        }
    }
}
