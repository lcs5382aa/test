package yg.devp.util;

public class SignalDTO {
    private int signal1;
    private int signal2;
    private int signal3;

    public SignalDTO() {
        this.signal1 = 1;
        this.signal2 = 1;
        this.signal3 = 1;
    }

    public int getSignal1() {
        return signal1;
    }

    public void setSignal1(int signal1) {
        this.signal1 = signal1;
    }

    public int getSignal2() {
        return signal2;
    }

    public void setSignal2(int signal2) {
        this.signal2 = signal2;
    }

    public int getSignal3() {
        return signal3;
    }

    public void setSignal3(int signal3) {
        this.signal3 = signal3;
    }

    public boolean isFull() {
        return (this.signal1 != 1 && this.signal2 != 1 && this.signal3 != 1);
    }

    public void empty(){
        this.signal1 = 1;
        this.signal2 = 1;
        this.signal3 = 1;
    }
}
