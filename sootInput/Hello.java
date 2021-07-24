package test;

import java.util.Random;
import benchmark.internal.BenchmarkN;
import benchmark.objects.A;
import benchmark.objects.B;

public class Hello {

  public static void main(String[] args) {
    Random r = new Random(1);
    int x = r.nextInt(100);
    BenchmarkN.alloc(1); 
    A a = new A();
    BenchmarkN.alloc(2);
    A b = new A();
    //BenchmarkN.alloc(3);
    //A c = new A();
    if(x > 50) x = 1;
    int y = 0;
    BenchmarkN.test(1, a); 
    BenchmarkN.test(2, b); 
    //BenchmarkN.test(3, c); 
  }
}
