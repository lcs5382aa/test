package yg.devp.util;

public class Useful {

    static public String LOG_COMM_SERVER = "LOG_COMM_SERVER";

    static public String URL_QUERY = "http://cclab.cbnu.ac.kr:8000/query/";
    static public String URL_SAVE = "http://cclab.cbnu.ac.kr:8000/saving/";
    static public String URL_LEARN = "http://cclab.cbnu.ac.kr:8000/learning/";

    static final public int PERMISSION_REQUEST_COARSE_LOCATION = 456;

    static public String printByteArray(byte[] bytes) {
        // print BLE bytes to array
        String re = "[";
        for (Byte obj : bytes) {
            re += String.format("%02X,", obj);
        }
        re += "]";
        return re;
    }

}
