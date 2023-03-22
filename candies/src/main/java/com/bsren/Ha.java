package com.bsren;

public class Ha {

    A a;
    public Ha(){
        this.a = new A(this);
        System.out.println(a);
        System.out.println(this);
        System.out.println(a.getHa());
    }

    public static void main(String[] args) {
        Ha ha = new Ha();
    }
}




class A{
    private Ha ha;

    public A(Ha ha) {
        this.ha = ha;
    }

    public Ha getHa() {
        return ha;
    }
}

