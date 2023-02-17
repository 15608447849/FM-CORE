package bottle.log;


import static bottle.log.PrintLogThread.addMessageQueue;

public class MLOG {
    public static void info(Object... objects){
        try {
            if (objects!=null && objects.length>0){
                StringBuilder stringBuilder = new StringBuilder();
                for(Object o : objects){
                    stringBuilder.append(o).append("\t");
                }
                stringBuilder.deleteCharAt(stringBuilder.length()-1);
                addMessageQueue(new LogBean(LogLevel.info,stringBuilder));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void error(String message,Throwable e){
        try {
            addMessageQueue(new LogBean(LogLevel.error,message,e));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
