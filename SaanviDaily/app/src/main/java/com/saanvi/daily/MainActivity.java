package com.saanvi.daily;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private DataStore db;
    private LinearLayout root;
    private boolean parentUnlocked=false;

    private final int BG=Color.rgb(10,15,29), TEXT=Color.rgb(246,248,252), MUTED=Color.rgb(157,170,192);
    private final int CARD=Color.rgb(23,31,52), CARD2=Color.rgb(29,38,64), LINE=Color.rgb(53,65,101);
    private final int PURPLE=Color.rgb(139,92,246), PURPLE_SOFT=Color.rgb(52,42,86);
    private final int GOLD=Color.rgb(251,191,36), GREEN=Color.rgb(52,211,153), RED=Color.rgb(248,113,113);
    private final int BLUE=Color.rgb(56,189,248), PINK=Color.rgb(244,114,182);

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        db=new DataStore(this);
        Window w=getWindow();
        w.setStatusBarColor(BG);w.setNavigationBarColor(BG);
        w.getDecorView().setSystemUiVisibility(0);
        showSplash();
    }

    private void showSplash(){
        FrameLayout f=new FrameLayout(this);f.setBackground(gradient(Color.rgb(8,13,28),Color.rgb(43,25,92),GradientDrawable.Orientation.TL_BR));
        LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setGravity(Gravity.CENTER);
        f.addView(c,new FrameLayout.LayoutParams(-1,-1));
        TextView coin=txt("★",64,true,PURPLE);coin.setGravity(Gravity.CENTER);coin.setBackground(circle(GOLD));
        c.addView(coin,new LinearLayout.LayoutParams(dp(112),dp(112)));
        TextView t=txt("Saanvi Daily",34,true,TEXT);t.setGravity(Gravity.CENTER);t.setPadding(0,dp(22),0,0);c.addView(t);
        TextView s=txt("Routine • Responsibility • Rewards",15,false,MUTED);s.setGravity(Gravity.CENTER);c.addView(s);
        setContentView(f);
        coin.setScaleX(.5f);coin.setScaleY(.5f);coin.setAlpha(0);
        coin.animate().alpha(1).scaleX(1).scaleY(1).setDuration(650).setInterpolator(new BounceInterpolator()).start();
        new Handler().postDelayed(this::showChild,900);
    }

    private void base(){
        ScrollView sc=new ScrollView(this);sc.setFillViewport(true);sc.setClipToPadding(false);sc.setBackgroundColor(BG);
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(BG);
        root.setPadding(dp(18),dp(20),dp(18),dp(44));
        root.setBackground(gradient(Color.rgb(9,14,28),Color.rgb(18,26,48),GradientDrawable.Orientation.TOP_BOTTOM));
        sc.addView(root,new ScrollView.LayoutParams(-1,-2));setContentView(sc);

        sc.setOnApplyWindowInsetsListener((v,insets)->{
            int top=insets.getSystemWindowInsetTop();
            int bottom=insets.getSystemWindowInsetBottom();
            root.setPadding(dp(18),dp(20)+top,dp(18),dp(44)+bottom+dp(16));
            return insets;
        });
        sc.requestApplyInsets();
    }

    private TextView txt(String s,float sp,boolean bold,int color){
        TextView v=new TextView(this);v.setText(s);v.setTextSize(sp);v.setTextColor(color);v.setLineSpacing(0,1.08f);
        if(bold)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return v;
    }
    private TextView txt(String s,float sp,boolean bold){return txt(s,sp,bold,TEXT);}
    private Button btn(String s,int bg,int fg){
        Button b=new Button(this);b.setAllCaps(false);b.setText(s);b.setTextSize(14);b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        b.setTextColor(fg);b.setMinHeight(dp(50));b.setPadding(dp(12),dp(8),dp(12),dp(8));b.setBackground(round(bg,15));return b;
    }
    private LinearLayout card(){
        LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(dp(17),dp(15),dp(17),dp(15));
        c.setBackground(border(CARD,LINE,22));c.setElevation(0);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,dp(7),0,dp(7));c.setLayoutParams(lp);return c;
    }
    private View section(String title,String sub){
        LinearLayout x=new LinearLayout(this);x.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,dp(17),0,dp(3));x.setLayoutParams(lp);
        x.addView(txt(title,21,true,PURPLE));x.addView(txt(sub,13,false,MUTED));return x;
    }
    private Space gap(int h){Space s=new Space(this);s.setLayoutParams(new LinearLayout.LayoutParams(1,dp(h)));return s;}
    private GradientDrawable round(int color,float radius){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(radius));return g;}
    private GradientDrawable border(int fill,int stroke,float radius){GradientDrawable g=round(fill,radius);g.setStroke(dp(1),stroke);return g;}
    private GradientDrawable circle(int color){GradientDrawable g=new GradientDrawable();g.setShape(GradientDrawable.OVAL);g.setColor(color);return g;}
    private GradientDrawable gradient(int a,int b,GradientDrawable.Orientation o){GradientDrawable g=new GradientDrawable(o,new int[]{a,b});g.setCornerRadius(dp(24));return g;}
    private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    private String money(double v){return Math.abs(v-Math.rint(v))<.005?String.format(Locale.US,"%.0f",v):String.format(Locale.US,"%.2f",v);}
    private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}

    // CHILD
    private void showChild(){
        parentUnlocked=false;base();
        root.addView(txt(greeting()+", "+db.get("child_name","Saanvi")+"! 👋",29,true));
        root.addView(txt(new SimpleDateFormat("EEEE, dd MMMM",Locale.US).format(new Date()),14,false,MUTED));

        int[] p=db.progress();int total=p[0],done=p[1];int pct=total==0?0:(int)Math.round(done*100.0/total);
        LinearLayout prog=card();prog.setBackground(gradient(Color.rgb(77,47,153),Color.rgb(24,91,157),GradientDrawable.Orientation.LEFT_RIGHT));LinearLayout line=new LinearLayout(this);line.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout left=new LinearLayout(this);left.setOrientation(LinearLayout.VERTICAL);left.addView(txt("TODAY'S PROGRESS",12,true,MUTED));left.addView(txt(done+" / "+total+" tasks completed",20,true));
        line.addView(left,new LinearLayout.LayoutParams(0,-2,1));TextView badge=txt(pct+"%",16,true,PURPLE);badge.setGravity(Gravity.CENTER);badge.setBackground(round(PURPLE_SOFT,18));line.addView(badge,new LinearLayout.LayoutParams(dp(66),dp(40)));prog.addView(line);
        ProgressBar bar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);bar.setMax(100);bar.setProgress(pct);bar.setProgressTintList(ColorStateList.valueOf(PURPLE));
        LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,dp(8));bp.setMargins(0,dp(13),0,dp(8));prog.addView(bar,bp);
        String preview=db.finalized()?"Today's reward is finalised ✓":"Current reward preview: 🪙 "+money(db.previewReward())+" / "+money(db.dailyMax());
        prog.addView(txt(preview,14,true,db.finalized()?GREEN:GOLD));root.addView(prog);

        root.addView(section("My Reward Boxes","Watch the coins grow"));
        addWalletShowcase();

        root.addView(section("Today's Routine",db.finalized()?"Day closed by parent":"Tap Done after finishing a task"));
        List<DataStore.Task> tasks=db.tasks();
        if(tasks.isEmpty()){LinearLayout e=card();e.addView(txt("No routine yet.",18,true));e.addView(txt("Parent can create tasks from Parent Login.",13,false,MUTED));root.addView(e);}
        for(DataStore.Task t:tasks) addChildTask(t);

        root.addView(gap(10));
        Button rewards=btn("🏆 Rewards & Achievements",PURPLE_SOFT,PURPLE);rewards.setOnClickListener(v->showAchievements());root.addView(rewards);
        root.addView(gap(8));
        Button parent=btn("👨‍👩‍👧 Parent Login",Color.rgb(30,34,40),Color.WHITE);parent.setOnClickListener(v->askPin());root.addView(parent);
        root.addView(gap(18));
        TextView note=txt("Coins are fun reward points. Real money is handled by the parent.",12,false,MUTED);note.setGravity(Gravity.CENTER);root.addView(note);
    }

    private void addChildTask(DataStore.Task t){
        boolean done=db.childDone(t.id);int approval=db.approval(t.id);
        LinearLayout c=card();LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);
        TextView time=txt(t.start+"\n"+t.end,12,true,PURPLE);time.setGravity(Gravity.CENTER);time.setBackground(round(PURPLE_SOFT,12));time.setPadding(dp(8),dp(7),dp(8),dp(7));r.addView(time,new LinearLayout.LayoutParams(dp(72),-2));
        LinearLayout info=new LinearLayout(this);info.setOrientation(LinearLayout.VERTICAL);info.setPadding(dp(12),0,dp(6),0);info.addView(txt(t.title,16,true));
        String st=approval==1?"✓ Approved by parent":approval==0?"✕ Not approved":done?"Done • waiting for parent":"Not completed yet";
        info.addView(txt(st,12,false,approval==1?GREEN:approval==0?RED:done?BLUE:MUTED));r.addView(info,new LinearLayout.LayoutParams(0,-2,1));
        Button b=btn(done?"Done ✓":"Done",done?Color.rgb(230,245,236):PURPLE,done?GREEN:Color.WHITE);
        b.setEnabled(!done&&!db.finalized());b.setOnClickListener(v->{db.markDone(t.id);showChild();});r.addView(b,new LinearLayout.LayoutParams(dp(88),dp(48)));c.addView(r);root.addView(c);
    }

    private void addWalletShowcase(){
        LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(walletMini("❤️ Helping",db.balance(DataStore.HELP),PINK),new LinearLayout.LayoutParams(0,-2,1));
        row.addView(walletMini("🎁 Weekly",db.balance(DataStore.CHILD)+db.balance(DataStore.SISTER),BLUE),new LinearLayout.LayoutParams(0,-2,1));
        row.addView(walletMini("🏦 Monthly",db.balance(DataStore.MONTH),PURPLE),new LinearLayout.LayoutParams(0,-2,1));
        root.addView(row);
    }
    private View walletMini(String title,double amount,int accent){
        LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setGravity(Gravity.CENTER);c.setPadding(dp(6),dp(12),dp(6),dp(12));c.setBackground(border(CARD2,accent,20));
        CoinJarView jar=new CoinJarView(this);jar.setAccent(accent);jar.setBalance(amount);c.addView(jar,new LinearLayout.LayoutParams(dp(86),dp(102)));
        TextView t=txt(title,11,true,accent);t.setGravity(Gravity.CENTER);c.addView(t);TextView a=txt("🪙 "+money(amount),15,true);a.setGravity(Gravity.CENTER);c.addView(a);return c;
    }

    private void askPin(){
        EditText e=new EditText(this);e.setHint("4-digit Parent PIN");e.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);e.setPadding(dp(18),0,dp(18),0);
        new AlertDialog.Builder(this).setTitle("Parent Login").setMessage("Enter Parent PIN").setView(e).setPositiveButton("Login",(d,w)->{
            if(e.getText().toString().equals(db.get("pin","1234"))){parentUnlocked=true;showParent();}else toast("Wrong PIN");
        }).setNegativeButton("Cancel",null).show();
    }

    // PARENT DASHBOARD
    private void showParent(){
        if(!parentUnlocked){showChild();return;}base();
        LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout h=new LinearLayout(this);h.setOrientation(LinearLayout.VERTICAL);h.addView(txt("Parent Dashboard",28,true));h.addView(txt("Approve routine • manage rewards",13,false,MUTED));top.addView(h,new LinearLayout.LayoutParams(0,-2,1));
        Button out=btn("Logout",PURPLE_SOFT,PURPLE);out.setOnClickListener(v->showChild());top.addView(out,new LinearLayout.LayoutParams(dp(86),dp(48)));root.addView(top);

        int[] pr=db.progress();LinearLayout s=card();s.addView(txt("Today",18,true));s.addView(txt(pr[2]+" approved of "+pr[0]+" tasks • "+pr[1]+" marked Done",14,false,MUTED));
        s.addView(txt(db.finalized()?"Reward finalised":"Reward preview: 🪙 "+money(db.previewReward()),18,true,db.finalized()?GREEN:GOLD));root.addView(s);

        root.addView(section("Task Approval","Approve only after checking the task"));
        for(DataStore.Task t:db.tasks()) addApprovalCard(t);

        if(!db.finalized()){
            root.addView(gap(8));Button finalise=btn("✓ FINALISE TODAY'S REWARD",GREEN,Color.WHITE);
            finalise.setOnClickListener(v->confirmFinalize());root.addView(finalise);
        }else{
            LinearLayout closed=card();closed.addView(txt("✓ Today is closed",17,true,GREEN));closed.addView(txt("Tomorrow starts with a fresh routine status.",13,false,MUTED));root.addView(closed);
        }

        root.addView(section("Routine Setup","Create, edit or delete daily tasks"));
        Button manage=btn("📝 Manage Daily Routine",PURPLE,Color.WHITE);manage.setOnClickListener(v->showRoutineManager());root.addView(manage);

        root.addView(section("Reward Wallet","Payments clear the current balance; history remains"));
        addParentWallet("❤️ Helping Others",DataStore.HELP,PINK,"Helping amount paid / donated");
        addParentWallet("🎁 Saanvi Weekly Pocket",DataStore.CHILD,BLUE,"Saanvi weekly pocket money paid");
        addParentWallet("🎁 Sister Share",DataStore.SISTER,GOLD,"Sister share paid");
        addParentWallet("🏦 Monthly Expense",DataStore.MONTH,PURPLE,"Monthly expense redeemed");

        root.addView(section("More","Reports, settings and history"));
        LinearLayout actions=new LinearLayout(this);
        Button rep=btn("📊 Reports",PURPLE_SOFT,PURPLE);rep.setOnClickListener(v->showReports());actions.addView(rep,new LinearLayout.LayoutParams(0,dp(52),1));
        Space sp=gap(1);sp.setLayoutParams(new LinearLayout.LayoutParams(dp(8),1));actions.addView(sp);
        Button set=btn("⚙ Settings",Color.WHITE,TEXT);set.setBackground(border(Color.WHITE,Color.rgb(220,224,230),15));set.setOnClickListener(v->showSettings());actions.addView(set,new LinearLayout.LayoutParams(0,dp(52),1));root.addView(actions);
    }

    private void addApprovalCard(DataStore.Task t){
        boolean done=db.childDone(t.id);int a=db.approval(t.id);LinearLayout c=card();
        c.addView(txt(t.start+"–"+t.end+"  •  "+t.title,16,true));c.addView(txt(done?"Saanvi marked Done":"Saanvi has not marked Done",12,false,done?BLUE:MUTED));
        LinearLayout r=new LinearLayout(this);r.setPadding(0,dp(10),0,0);
        Button yes=btn(a==1?"Approved ✓":"Approve",a==1?GREEN:Color.rgb(232,248,239),a==1?Color.WHITE:GREEN);
        Button no=btn(a==0?"Not approved ✓":"Not Done",a==0?RED:Color.rgb(255,237,238),a==0?Color.WHITE:RED);
        yes.setEnabled(!db.finalized());no.setEnabled(!db.finalized());
        yes.setOnClickListener(v->{db.setApproval(t.id,1);showParent();});no.setOnClickListener(v->{db.setApproval(t.id,0);showParent();});
        r.addView(yes,new LinearLayout.LayoutParams(0,dp(48),1));Space x=new Space(this);r.addView(x,new LinearLayout.LayoutParams(dp(8),1));r.addView(no,new LinearLayout.LayoutParams(0,dp(48),1));c.addView(r);root.addView(c);
    }

    private void confirmFinalize(){
        int[] p=db.progress();int missed=p[0]-p[2];double reward=db.previewReward();
        new AlertDialog.Builder(this).setTitle("Finalise today?")
            .setMessage(p[2]+" of "+p[0]+" tasks approved\nMissed: "+missed+"\nToday's reward: 🪙 "+money(reward)+"\n\n1/2/3/4 missed = 10/20/30/40% deduction. 5+ missed = 0.")
            .setPositiveButton("FINALISE",(d,w)->{
                double beforeH=db.balance(DataStore.HELP),beforeW=db.balance(DataStore.CHILD)+db.balance(DataStore.SISTER),beforeM=db.balance(DataStore.MONTH);
                double earned=db.finalizeToday();showRewardAnimation(earned,beforeH,beforeW,beforeM);
            }).setNegativeButton("Cancel",null).show();
    }

    private void addParentWallet(String title,String bucket,int accent,String note){
        LinearLayout c=card();LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout info=new LinearLayout(this);info.setOrientation(LinearLayout.VERTICAL);info.addView(txt(title,16,true));info.addView(txt("🪙 "+money(db.balance(bucket)),23,true,accent));r.addView(info,new LinearLayout.LayoutParams(0,-2,1));
        Button pay=btn("Mark Paid",accent,Color.WHITE);pay.setOnClickListener(v->confirmPayout(bucket,note));r.addView(pay,new LinearLayout.LayoutParams(dp(108),dp(48)));c.addView(r);root.addView(c);
    }
    private void confirmPayout(String bucket,String note){
        double b=db.balance(bucket);if(b<=0){toast("Balance is already empty");return;}
        new AlertDialog.Builder(this).setTitle("Confirm payment").setMessage("Current balance: 🪙 "+money(b)+"\n\nAfter confirmation this balance becomes 0. History will stay saved.")
            .setPositiveButton("MARK AS PAID",(d,w)->{db.redeem(bucket,note);showParent();toast("Payment saved ✓");}).setNegativeButton("Cancel",null).show();
    }

    // ROUTINE MANAGER
    private void showRoutineManager(){
        if(!parentUnlocked){showChild();return;}base();backButton("← Parent Dashboard",this::showParent);
        root.addView(gap(12));root.addView(txt("Daily Routine",28,true));root.addView(txt("These tasks repeat every day. Parent can change them anytime.",13,false,MUTED));
        Button add=btn("+ Add New Task",PURPLE,Color.WHITE);add.setOnClickListener(v->taskDialog(null));root.addView(gap(12));root.addView(add);
        root.addView(section("Current Tasks","Tap Edit to change title or time"));
        for(DataStore.Task t:db.tasks()){
            LinearLayout c=card();c.addView(txt(t.start+"–"+t.end,13,true,PURPLE));c.addView(txt(t.title,17,true));
            LinearLayout r=new LinearLayout(this);r.setPadding(0,dp(10),0,0);
            Button edit=btn("Edit",PURPLE_SOFT,PURPLE);edit.setOnClickListener(v->taskDialog(t));Button del=btn("Delete",Color.rgb(255,237,238),RED);
            del.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Delete task?").setMessage(t.title).setPositiveButton("Delete",(d,w)->{db.deleteTask(t.id);showRoutineManager();}).setNegativeButton("Cancel",null).show());
            r.addView(edit,new LinearLayout.LayoutParams(0,dp(48),1));Space z=new Space(this);r.addView(z,new LinearLayout.LayoutParams(dp(8),1));r.addView(del,new LinearLayout.LayoutParams(0,dp(48),1));c.addView(r);root.addView(c);
        }
    }

    private void taskDialog(DataStore.Task existing){
        LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(dp(20),dp(8),dp(20),0);
        EditText title=new EditText(this);title.setHint("Task title");title.setText(existing==null?"":existing.title);box.addView(title);
        final String[] start={existing==null?"07:00":existing.start},end={existing==null?"08:00":existing.end};
        Button bs=btn("Start: "+start[0],PURPLE_SOFT,PURPLE),be=btn("End: "+end[0],PURPLE_SOFT,PURPLE);
        bs.setOnClickListener(v->pickTime(start[0],x->{start[0]=x;bs.setText("Start: "+x);}));
        be.setOnClickListener(v->pickTime(end[0],x->{end[0]=x;be.setText("End: "+x);}));
        box.addView(gap(8));box.addView(bs);box.addView(gap(7));box.addView(be);
        new AlertDialog.Builder(this).setTitle(existing==null?"Create Task":"Edit Task").setView(box).setPositiveButton("Save",(d,w)->{
            String t=title.getText().toString().trim();if(t.isEmpty()){toast("Task title is required");return;}
            if(existing==null)db.addTask(t,start[0],end[0]);else db.updateTask(existing.id,t,start[0],end[0]);showRoutineManager();
        }).setNegativeButton("Cancel",null).show();
    }
    private interface TimeResult{void set(String s);}
    private void pickTime(String current,TimeResult cb){
        int h=7,m=0;try{String[] a=current.split(":");h=Integer.parseInt(a[0]);m=Integer.parseInt(a[1]);}catch(Exception ignored){}
        new TimePickerDialog(this,(v,hh,mm)->cb.set(String.format(Locale.US,"%02d:%02d",hh,mm)),h,m,true).show();
    }

    // REWARD ANIMATION
    private void showRewardAnimation(double earned,double beforeH,double beforeW,double beforeM){
        base();TextView tag=txt(earned>0?"🎉 GREAT JOB!":"🌤 TOMORROW IS A NEW DAY",14,true,earned>0?GREEN:MUTED);tag.setGravity(Gravity.CENTER);root.addView(tag);
        TextView title=txt(earned>0?db.get("child_name","Saanvi")+" earned":"No coins today",31,true);title.setGravity(Gravity.CENTER);root.addView(title);
        TextView big=txt("🪙 "+money(earned),43,true,GOLD);big.setGravity(Gravity.CENTER);big.setAlpha(0);big.setScaleX(.5f);big.setScaleY(.5f);root.addView(big);
        big.animate().alpha(1).scaleX(1).scaleY(1).setDuration(700).setInterpolator(new BounceInterpolator()).start();
        if(earned>0){
            root.addView(txt("Coins are dropping into the reward boxes…",14,false,MUTED));
            animatedJar("❤️ Helping Others",beforeH,beforeH+earned*.10,PINK);
            animatedJar("🎁 Weekly Pocket Money",beforeW,beforeW+earned*.40,BLUE);
            animatedJar("🏦 Monthly Expense",beforeM,beforeM+earned*.50,PURPLE);
        }else{
            LinearLayout c=card();c.addView(txt("5 or more tasks were not approved, so today's reward is 0.",16,true,RED));c.addView(txt("Tomorrow starts fresh again.",13,false,MUTED));root.addView(c);
        }
        root.addView(gap(10));Button home=btn("Back to Saanvi Home",PURPLE,Color.WHITE);home.setOnClickListener(v->showChild());root.addView(home);
        root.addView(gap(8));Button pd=btn("Parent Dashboard",PURPLE_SOFT,PURPLE);pd.setOnClickListener(v->{parentUnlocked=true;showParent();});root.addView(pd);
    }

    private void animatedJar(String label,double from,double to,int accent){
        LinearLayout c=card();LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);
        CoinJarView jar=new CoinJarView(this);jar.setAccent(accent);jar.setBalance(from);r.addView(jar,new LinearLayout.LayoutParams(dp(120),dp(140)));
        LinearLayout inf=new LinearLayout(this);inf.setOrientation(LinearLayout.VERTICAL);inf.setPadding(dp(12),0,0,0);inf.addView(txt(label,16,true));
        TextView val=txt("🪙 "+money(from),25,true,accent);inf.addView(val);r.addView(inf,new LinearLayout.LayoutParams(0,-2,1));c.addView(r);root.addView(c);
        jar.postDelayed(()->jar.animateTo(to),300);
        ValueAnimator a=ValueAnimator.ofFloat((float)from,(float)to);a.setStartDelay(300);a.setDuration(900);a.addUpdateListener(v->val.setText("🪙 "+money((Float)v.getAnimatedValue())));a.start();
    }

    // ACHIEVEMENTS / REPORTS
    private void showAchievements(){
        base();backButton("← Saanvi Home",this::showChild);root.addView(gap(12));root.addView(txt("Rewards & Achievements 🏆",28,true));
        LinearLayout c=card();c.addView(txt("🔥 Perfect-day streak",15,true,MUTED));c.addView(txt(db.streak()+" days",30,true,GOLD));c.addView(txt("⭐ Perfect days: "+db.perfectDays(),16,true));c.addView(txt("🪙 Total earned: "+money(db.totalEarned()),16,true,PURPLE));root.addView(c);
        addWalletShowcase();
        LinearLayout badges=card();badges.addView(txt("Badges",19,true));badges.addView(txt(db.perfectDays()>=1?"⭐ Perfect Day — unlocked":"🔒 Perfect Day — complete every task",14,true));
        badges.addView(txt(db.perfectDays()>=7?"🏆 Perfect Week — unlocked":"🔒 Perfect Week — collect 7 perfect days",14,true));
        badges.addView(txt(db.totalEarned()>=500?"💰 Saver Star — unlocked":"🔒 Saver Star — earn 500 coins",14,true));root.addView(badges);
    }

    private void showReports(){
        if(!parentUnlocked){showChild();return;}base();backButton("← Parent Dashboard",this::showParent);root.addView(gap(12));root.addView(txt("Reports & History 📊",28,true));
        LinearLayout sum=card();sum.addView(txt("Summary",19,true));sum.addView(txt("Finalised days: "+db.totalFinalizedDays(),15,true));sum.addView(txt("Perfect days: "+db.perfectDays(),15,true,GREEN));sum.addView(txt("Current streak: 🔥 "+db.streak(),15,true,GOLD));sum.addView(txt("Total earned: 🪙 "+money(db.totalEarned()),18,true,PURPLE));root.addView(sum);

        root.addView(section("Daily History","Green = perfect • yellow = partial • red = no reward"));
        JSONArray h=db.history();
        for(int i=h.length()-1;i>=0;i--){
            try{
                JSONObject o=h.getJSONObject(i);int missed=o.optInt("missed");double rw=o.optDouble("reward");
                LinearLayout c=card();LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);
                LinearLayout inf=new LinearLayout(this);inf.setOrientation(LinearLayout.VERTICAL);
                String icon=missed==0?"🟢":rw==0?"🔴":"🟡";inf.addView(txt(icon+" "+db.prettyDate(o.optString("date")),14,true));inf.addView(txt(o.optInt("approved")+"/"+o.optInt("total")+" approved • "+missed+" missed",12,false,MUTED));
                r.addView(inf,new LinearLayout.LayoutParams(0,-2,1));r.addView(txt("🪙 "+money(rw),16,true,rw==0?RED:GOLD));c.addView(r);root.addView(c);
            }catch(Exception ignored){}
        }
        root.addView(section("Payout History","Paid balances are kept here"));
        JSONArray a=db.payouts();
        for(int i=a.length()-1;i>=0;i--){
            try{
                JSONObject o=a.getJSONObject(i);LinearLayout c=card();c.addView(txt(bucketName(o.optString("bucket"))+"  −🪙 "+money(o.optDouble("amount")),15,true,RED));
                c.addView(txt(new SimpleDateFormat("dd MMM yyyy, hh:mm a",Locale.US).format(new Date(o.optLong("ts")))+" • "+o.optString("note"),12,false,MUTED));root.addView(c);
            }catch(Exception ignored){}
        }
        if(h.length()==0&&a.length()==0){LinearLayout e=card();e.addView(txt("No history yet.",15,true));root.addView(e);}
    }

    private String bucketName(String x){
        if(DataStore.HELP.equals(x))return "❤️ Helping";if(DataStore.CHILD.equals(x))return "🎁 Saanvi Weekly";if(DataStore.SISTER.equals(x))return "🎁 Sister Share";return "🏦 Monthly";
    }

    // SETTINGS
    private void showSettings(){
        if(!parentUnlocked){showChild();return;}base();backButton("← Parent Dashboard",this::showParent);root.addView(gap(12));root.addView(txt("Parent Settings ⚙",28,true));
        LinearLayout names=card();names.addView(txt("Family Names",19,true));EditText child=new EditText(this);child.setHint("Child name");child.setText(db.get("child_name","Saanvi"));
        EditText sister=new EditText(this);sister.setHint("Sister name");sister.setText(db.get("sister_name","Sister"));names.addView(child);names.addView(sister);
        Button sn=btn("Save Names",PURPLE,Color.WHITE);sn.setOnClickListener(v->{if(child.getText().toString().trim().isEmpty()){child.setError("Required");return;}db.set("child_name",child.getText().toString().trim());db.set("sister_name",sister.getText().toString().trim().isEmpty()?"Sister":sister.getText().toString().trim());toast("Names saved");});names.addView(sn);root.addView(names);

        LinearLayout rule=card();rule.addView(txt("Daily Reward Rule",19,true));rule.addView(txt("1/2/3/4 missed → 10/20/30/40% cut. 5+ missed → 0. Distribution: Helping 10%, Saanvi weekly 20%, Sister 20%, Monthly 50%.",13,false,MUTED));
        EditText max=new EditText(this);max.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);max.setHint("Daily maximum");max.setText(db.get("daily_max","50"));rule.addView(max);
        Button sr=btn("Save Daily Maximum",PURPLE,Color.WHITE);sr.setOnClickListener(v->{try{double x=Double.parseDouble(max.getText().toString());if(x<=0)throw new Exception();db.set("daily_max",String.valueOf(x));toast("Reward rule saved");}catch(Exception e){max.setError("Enter valid amount");}});rule.addView(sr);root.addView(rule);

        LinearLayout pin=card();pin.addView(txt("Change Parent PIN",19,true));EditText old=pinEdit("Current PIN"),n1=pinEdit("New 4-digit PIN"),n2=pinEdit("Confirm new PIN");pin.addView(old);pin.addView(n1);pin.addView(n2);
        Button cp=btn("Change PIN",Color.rgb(30,34,40),Color.WHITE);cp.setOnClickListener(v->{String a=old.getText().toString(),b=n1.getText().toString(),c=n2.getText().toString();if(!a.equals(db.get("pin","1234"))){old.setError("Wrong current PIN");return;}if(!b.matches("\\d{4}")){n1.setError("Use exactly 4 digits");return;}if(!b.equals(c)){n2.setError("PIN does not match");return;}db.set("pin",b);old.setText("");n1.setText("");n2.setText("");toast("PIN changed ✓");});pin.addView(cp);root.addView(pin);

        LinearLayout about=card();about.addView(txt("Saanvi Daily v2.2 Full",18,true));about.addView(txt("Offline-first • No ads • No real-money payment processing\nAll routine and reward data stays on this device.",13,false,MUTED));root.addView(about);
    }
    private EditText pinEdit(String hint){EditText e=new EditText(this);e.setHint(hint);e.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);return e;}

    private void backButton(String label,Runnable action){
        Button b=btn(label,Color.WHITE,TEXT);b.setBackground(border(Color.WHITE,Color.rgb(220,224,230),15));b.setOnClickListener(v->action.run());root.addView(b);
    }
    private String greeting(){
        int h=Integer.parseInt(new SimpleDateFormat("HH",Locale.US).format(new Date()));
        if(h<12)return "Good morning";if(h<17)return "Good afternoon";return "Good evening";
    }

    @Override public void onBackPressed(){
        if(parentUnlocked)showParent();else super.onBackPressed();
    }
}
