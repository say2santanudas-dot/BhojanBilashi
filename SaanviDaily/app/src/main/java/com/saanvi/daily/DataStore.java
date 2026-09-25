package com.saanvi.daily;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataStore {
    private final SharedPreferences p;
    public static final String HELP="help", CHILD="child", SISTER="sister", MONTH="month";

    public static class Task {
        public long id;
        public String title, start, end;
        public Task(long i,String t,String s,String e){id=i;title=t;start=s;end=e;}
    }

    public DataStore(Context c){
        p=c.getSharedPreferences("saanvi_daily",Context.MODE_PRIVATE);
        if(!p.getBoolean("seeded",false)){
            set("child_name","Saanvi"); set("sister_name","Sister"); set("pin","1234"); set("daily_max","50");
            List<Task> d=new ArrayList<>();
            d.add(new Task(101,"Brush, Toilet & Breakfast","06:00","07:00"));
            d.add(new Task(102,"Morning Study","07:15","08:30"));
            d.add(new Task(103,"Get Ready / School Routine","08:30","09:00"));
            d.add(new Task(104,"Homework / Revision","17:00","18:00"));
            d.add(new Task(105,"Dinner & Bedtime Routine","20:30","21:30"));
            saveTasks(d);
            p.edit().putBoolean("seeded",true).apply();
        }
    }

    public String get(String k,String def){ return p.getString(k,def); }
    public void set(String k,String v){ p.edit().putString(k,v).apply(); }
    public double dailyMax(){ try{return Double.parseDouble(get("daily_max","50"));}catch(Exception e){return 50;} }

    public List<Task> tasks(){
        ArrayList<Task> out=new ArrayList<>();
        try{
            JSONArray a=new JSONArray(get("tasks","[]"));
            for(int i=0;i<a.length();i++){
                JSONObject o=a.getJSONObject(i);
                out.add(new Task(o.getLong("id"),o.getString("title"),o.getString("start"),o.getString("end")));
            }
        }catch(Exception ignored){}
        return out;
    }
    public void saveTasks(List<Task> list){
        JSONArray a=new JSONArray();
        try{
            for(Task t:list){
                JSONObject o=new JSONObject();
                o.put("id",t.id);o.put("title",t.title);o.put("start",t.start);o.put("end",t.end);a.put(o);
            }
        }catch(Exception ignored){}
        set("tasks",a.toString());
    }
    public void addTask(String title,String start,String end){
        List<Task> x=tasks();x.add(new Task(System.currentTimeMillis(),title,start,end));saveTasks(x);
    }
    public void updateTask(long id,String title,String start,String end){
        List<Task> x=tasks();
        for(Task t:x) if(t.id==id){t.title=title;t.start=start;t.end=end;break;}
        saveTasks(x);
    }
    public void deleteTask(long id){
        List<Task> x=tasks();ArrayList<Task> n=new ArrayList<>();
        for(Task t:x) if(t.id!=id)n.add(t);
        saveTasks(n);
    }

    public String today(){ return new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(new Date()); }
    public String prettyDate(String ymd){
        try{return new SimpleDateFormat("EEE, dd MMM yyyy",Locale.US).format(new SimpleDateFormat("yyyy-MM-dd",Locale.US).parse(ymd));}
        catch(Exception e){return ymd;}
    }
    private JSONObject day(String date){
        try{
            String raw=get("day_"+date,"");
            JSONObject d=raw.isEmpty()?new JSONObject():new JSONObject(raw);
            if(!d.has("states"))d.put("states",new JSONObject());
            if(!d.has("finalized"))d.put("finalized",false);
            return d;
        }catch(Exception e){try{return new JSONObject().put("states",new JSONObject()).put("finalized",false);}catch(Exception x){return new JSONObject();}}
    }
    private void saveDay(String date,JSONObject d){ set("day_"+date,d.toString()); }
    private JSONObject state(JSONObject d,long id){
        try{
            JSONObject s=d.getJSONObject("states");String k=String.valueOf(id);
            if(!s.has(k))s.put(k,new JSONObject().put("done",false).put("approval",-1));
            return s.getJSONObject(k);
        }catch(Exception e){return new JSONObject();}
    }
    public boolean childDone(long id){
        try{return state(day(today()),id).optBoolean("done",false);}catch(Exception e){return false;}
    }
    public int approval(long id){
        try{return state(day(today()),id).optInt("approval",-1);}catch(Exception e){return -1;}
    }
    public void markDone(long id){
        String date=today();JSONObject d=day(date);
        if(d.optBoolean("finalized",false))return;
        try{state(d,id).put("done",true).put("approval",-1);saveDay(date,d);}catch(Exception ignored){}
    }
    public void setApproval(long id,int value){
        String date=today();JSONObject d=day(date);
        if(d.optBoolean("finalized",false))return;
        try{state(d,id).put("approval",value);saveDay(date,d);}catch(Exception ignored){}
    }
    public boolean finalized(){return day(today()).optBoolean("finalized",false);}
    public int[] progress(){
        List<Task> t=tasks();int done=0,approved=0;
        for(Task x:t){if(childDone(x.id))done++;if(approval(x.id)==1)approved++;}
        return new int[]{t.size(),done,approved};
    }
    public double previewReward(){
        int[] q=progress();int total=q[0],approved=q[2];if(total==0)return 0;
        int missed=total-approved;if(missed>=5)return 0;
        return dailyMax()*(1.0-(missed*.10));
    }

    public double finalizeToday(){
        String date=today();JSONObject d=day(date);
        if(d.optBoolean("finalized",false))return d.optDouble("reward",0);
        int[] q=progress();int total=q[0],approved=q[2],missed=total-approved;
        double reward= total==0?0:(missed>=5?0:dailyMax()*(1.0-missed*.10));
        try{
            d.put("finalized",true);d.put("reward",reward);d.put("total",total);d.put("approved",approved);d.put("missed",missed);saveDay(date,d);
            if(reward>0){
                addBalance(HELP,reward*.10); addBalance(CHILD,reward*.20); addBalance(SISTER,reward*.20); addBalance(MONTH,reward*.50);
            }
            JSONArray h=history();boolean exists=false;
            for(int i=0;i<h.length();i++)if(date.equals(h.getJSONObject(i).optString("date"))){exists=true;break;}
            if(!exists){JSONObject o=new JSONObject();o.put("date",date);o.put("total",total);o.put("approved",approved);o.put("missed",missed);o.put("reward",reward);h.put(o);set("history",h.toString());}
        }catch(Exception ignored){}
        return reward;
    }

    private double balRaw(String bucket){try{return Double.parseDouble(get("bal_"+bucket,"0"));}catch(Exception e){return 0;}}
    public double balance(String bucket){return balRaw(bucket);}
    private void addBalance(String bucket,double v){set("bal_"+bucket,String.valueOf(balRaw(bucket)+v));}

    public double redeem(String bucket,String note){
        double amt=balance(bucket); if(amt<=0)return 0;
        set("bal_"+bucket,"0");
        try{
            JSONArray a=payouts();JSONObject o=new JSONObject();
            o.put("ts",System.currentTimeMillis());o.put("bucket",bucket);o.put("amount",amt);o.put("note",note);a.put(o);set("payouts",a.toString());
        }catch(Exception ignored){}
        return amt;
    }

    public JSONArray history(){try{return new JSONArray(get("history","[]"));}catch(Exception e){return new JSONArray();}}
    public JSONArray payouts(){try{return new JSONArray(get("payouts","[]"));}catch(Exception e){return new JSONArray();}}

    public int perfectDays(){int n=0;try{JSONArray h=history();for(int i=0;i<h.length();i++)if(h.getJSONObject(i).optInt("missed",1)==0)n++;}catch(Exception ignored){}return n;}
    public int totalFinalizedDays(){return history().length();}
    public double totalEarned(){double x=0;try{JSONArray h=history();for(int i=0;i<h.length();i++)x+=h.getJSONObject(i).optDouble("reward",0);}catch(Exception ignored){}return x;}
    public int streak(){
        try{
            JSONArray h=history(); if(h.length()==0)return 0;
            int count=0;Calendar c=Calendar.getInstance();SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd",Locale.US);
            for(int offset=0;offset<366;offset++){
                String date=f.format(c.getTime());boolean found=false,perfect=false;
                for(int i=0;i<h.length();i++){JSONObject o=h.getJSONObject(i);if(date.equals(o.optString("date"))){found=true;perfect=o.optInt("missed",9)==0;break;}}
                if(found&&perfect)count++; else if(found||offset>0)break;
                c.add(Calendar.DAY_OF_MONTH,-1);
            }
            return count;
        }catch(Exception e){return 0;}
    }
}
