package bgu.spl.a2;
public class Logger {
    static boolean isEnable=true;

    public static void Log(String msg){
        if(isEnable) {
            System.out.println(msg);
        }
    }
}
