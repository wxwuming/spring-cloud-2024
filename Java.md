# JVM

## Oracle JDK 和Open JDK 有什么区别

Oracle JDK 是由 Oracle 公司提供的，包含了一些商业特性和支持服务；而 OpenJDK 是一个由社区驱动的开源项目，提供了免费的Java开发工具。

## JDK、JRE、JVM的关系

**JDK** = JRE + 开发工具集（例如Javac编译工具等）

**JRE** = JVM + Java SE 标准类库

## JVM跨语言的平台

不论程序通过何种代码编写，只关心字节码文件

Java不是最强大的语言，但是JVM是最强大的虚拟机

## 类加载器

加载->验证->准备->解析->初始化

加载：通过类的全限定名（包名+类名）获取到该类的.class字节码文件，利用字节码文件创建Class对象

验证：确保被加载的类的字节码符合JVM规范，不会影响JVM，如验证字节码文件的结构、语义、操作的合法性

准备：为类的静态变量分配内存，并设置初始默认值，在方法区中进行分配

解析：将符号引用转化为直接引用

初始化：执行类的构造器方法init()

## 类加载器的分类

JDK9=》扩展类加载器（Extension Class Loader）被平台类加载器（Platform Class Loader）取代。

- jdk8 时，拓展类加载器和用户类加载都是继承的 URLClassLoader，jdk11 之后，三个默认的累加载器都继承了BuiltinClassLoader

继承URLClassLoader（JDK17：BuiltinClassLoader）->继承SecureClassLoader->继承ClassLoader

BootStrapClassLoader：引导类加载器，定义核心Java SE和JDK模块

ExtClassLoader：扩展类加载器 （JDK17：PlatformClassLoader ），定义部分Java SE和JDK模块

AppClassLoader：应用类加载器，定义CLASSPATH上的类和模块路径中的模块

## 双亲委派

- 避免类的重复加载
- 防止核心API被篡改

（由下至上委派，由上至下加载）当一个类加载器收到了类加载的请求的时候，他不会直接去加载指定的类，而是把这个请求委托给自己的父加载器去加载。只有父加载器无法加载这个类的时候，才会由当前这个加载器来负责类的加载。

类加载器继承自ClassLoader ： loadClass（双亲委派具体实现）、findClass（类加载）、defineClass（将byte字节流解析成Class对象）

## Tomcat为什么要自定义类加载器

Tomcat中部署多个应用，其中有同名不同功能的类，双亲委派导致类无法被多次加载，导致只有一个类生效了

自定义WebAppClassLoader，每个应用设置各自单独的类加载器，类加载的隔离、热部署、热加载

## 运行时数据区

1. **程序计数器 (Program Counter)**： 程序计数器是一块较小的内存空间，可以看作是当前线程所执行的字节码的行号指示器。在多线程环境下，每个线程都有自己的程序计数器，互不影响。不会出现内存溢出(OutOfMemoryError)的情况。
2. **Java 虚拟机栈 (JVM Stack)**： 每个线程在创建时都会被分配一个 Java 虚拟机栈。栈中存放着方法的栈帧，每个方法的调用都会创建一个栈帧，栈帧中存放着局部变量表、操作数栈、动态链接、方法出口等信息。栈帧的大小在编译时就已经确定。虚拟机栈不需要进行垃圾回收，方法开始执行时入栈，执行完成后出栈。执行完成后虚拟基栈就消失了。线程太多时，内存不够分派虚拟机栈导致内存溢出(OutOfMemoryError)，虚拟机栈中调用方法层数过多会导致栈溢出(StackOverflowErrow)
3. **本地方法栈 (Native Method Stack)**： 与 Java 虚拟机栈类似，不同之处在于本地方法栈为 Native 方法服务。
4. **Java 堆 (Java Heap)**： Java 堆是 Java 虚拟机中最大的一块内存区域，用于存放对象实例。在堆中可以被所有线程共享，因此在多线程环境下需要注意线程安全的问题。
5. **方法区 (Method Area)**： 方法区用于存放类信息、常量、静态变量、即时编译器编译后的代码等数据。在 Java 8 及之前的版本中，方法区被称为永久代 (Permanent Generation)，但在 Java 8 中已经被移除，并改为使用元空间 (Metaspace) 代替。**运行时常量池 (Runtime Constant Pool)**： 运行时常量池是方法区的一部分，用于存放编译期生成的字面量和符号引用。在类加载后，将常量池中的符号引用解析为直接引用。

程序计数器、java虚拟机栈、本地方法栈：线程单独所有

java堆、方法区：线程共享

## 堆

堆是JVM中最重要的一块区域，JVM规范中规定所有的对象和数组都应该存放在堆中，在执行字节码指令时，会把创建的对象存入堆中，对象对应的引用地址存入虚拟机栈中的栈帧中，不过当方法执行完之后，刚刚所创建的对象并不会立马被回收，而是要等JVM后台执行GC后，对象才会被回收。

新生代(GC频繁)：默认占1/3，Eden：伊甸区、S0：Survivor0、S1：Survivor1，占比8：1：1

老年代(GC不频繁)：默认占2/3

![image-20240329144348888](C:\Users\56511\AppData\Roaming\Typora\typora-user-images\image-20240329144348888.png)

新生代（Eden(伊甸区)、Survivor(幸存者区From区/To区)）Young GC / Minor GC

老年代 Old GC / Major GC 

Full GC

1.Eden区满了之后调用EdenGC进行垃圾回收，将存活对象存入S0，并标记经历过1次GC了

2.Eden区又满了之后触发EdenGC并对S0区垃圾对象进行回收，将S0区剩余存活对象存入S1区，并标记经历过2次GC了，Eden区存活对象存入S1区，并标记，此时S0区为空，等待下一次S0/S1的GC将剩余存活对象存入空区（标记复制算法）

3.S0/S1里面的存活对象的标记值超过15次时，进行GC操作时会将存活对象放入老年代区

### 特殊情况：

1.Eden区中的对象过大，S0/S1中放不下，则会直接放入老年代区

2.有个超大对象Eden区中放不下，直接放入老年代中

GC分代年龄为什么是15：Object Header采用4个bit位来保存年龄，0b1111最大为15

## 垃圾回收

垃圾是指在JVM中没有任何引用指向它的对象，防止内存泄露

### 垃圾标记阶段

#### 引用计数法

对每一个对象保存一个整型的引用计数器属性。用于记录对象被引用的情况。
对于一个对象A，只要有任何一个对象引用了A，则A的引用计数器就加1；当引用失效时，引用计数器就减1。只要对象的引用计数器的值为0，即表示对象A不能在被使用，可进行回收。

优点：实现简单，垃圾对象便于辨识；判定效率高，回收没有延迟性。

缺点：

（1）他需要单独的字段存储计数器，这样的做法增加了存储空间的开销。

（2）每次赋值都需要更新计数器，伴随着加法和减法操作，这增加了时间开销。

（3）引用计数器还有一个严重的问题，即无法处理循环引用的问题，这是一条致命的缺陷，导致在Java回收的垃圾回收器中没有使用这类算法。

#### 可达性分析法

1. 定义GC Roots：
- GC Roots是指在Java堆中能够直接或间接引用其他对象的一系列特殊对象集合。这些对象通常包括：

- 虚拟机栈（VM Stack）中的局部变量引用的对象。

- 方法区（Method Area）中类静态属性引用的对象。

- 方法区中常量池引用的对象。

- 本地方法栈（Native Method Stack）中JNI（Native Interface）引用的对象。

- JVM内部数据结构，如线程对象（Thread）中维护的引用。

2. 可达性分析过程：
- 从这些GC Roots开始，垃圾收集器会遍历整个对象图，沿着对象之间的引用关系向下搜索。

- 搜索过程中形成的引用链（Reference Chain）是从GC Roots到目标对象的所有引用路径。

- 如果一个对象可以从至少一个GC Root通过引用链到达，那么我们认为这个对象是可达的，也就是说它还“活着”，不会被垃圾回收。

- 相反，如果一个对象无法通过任何引用链与任何GC Roots相关联，则认为它是不可达的，是潜在的可回收对象。

3. 进一步处理：
- 对于那些被标记为不可达的对象，垃圾收集器会在后续的清理阶段将其回收，释放其所占用的内存空间。

- 注意，在实际的可达性分析过程中，还涉及到弱引用、软引用、虚引用和终结器引用等类型的引用处理，不同的引用类型对对象的可达性有不同的影响。

### 标记清除算法

当堆中的有效内存空间（available memory）被耗尽的时候，就会停止整个程序（也被称为stop the world），然后进行两项工作，第一项则是标记，第二项则是清除。

标记：Collector 从引用根节点开始遍历，标记所有被引用的对象。一般是在对象的 Header 中记录为可达对象。标记的是引用的对象，不是垃圾！！

清除：Collector 对堆内存从头到尾进行线性的遍历，如果发现某个对象在其 Header中 没有标记为可达对象，则将其回收。

**缺点**

- 标记清除算法的效率不算高。
- 在进行GC的时候，需要停止整个应用程序，用户体验较差。
- 这种方式清理出来的空闲内存是不连续的，产生内碎片，需要维护一个空闲列表。

复制算法

将原有的内存空间分为两块，每次只使用其中的一块，在垃圾回收时候，将正在使用的内存中的存活对象复制到为未使用的内存中，清除正在使用的内存块中的所有对象，交换两个内存的角色，完成垃圾回收。

复制算法完成后，第一块使用的那块内存完成清除，不管它存活还是未存活。都将它清理。

复制算法对空间有一定的浪费。只能使用一半的空间。

### 标记整理算法

根据老年代的特点，有人对“标记 - 清除”进行改进，提出了“标记 - 整理”算法。“标记 - 整理”算法的标记过程与“标记 - 清除”算法相同，但后续步骤不是直接对可回收对象进行清理，而是让所有存活的对象都向一端移动，然后直接清理掉端边界以外的内存。

分为三个阶段，标记、移动、清除，导致效率低

### 分代收集算法

不同对象的存活时长是不一样的，也就可以针对不同的对象采取不同的垃圾回收算法。

默认几乎所有的垃圾收集器都是采用分代收集算法进行垃圾回收的。

我们会把堆分为新生代和老年代:

新生代中的对象存活时间比较短，那么就可以利用复制算法，它适合垃圾对象比较多的情况。。老年代中的对象存活时间比较长，所以不太适合用复制算法，可以用标记-清除或标记-整理算法，比如:CMS垃圾收集器采用的就是标记-清除算法Serial Old垃圾收集器采用的就是标记-整理算法。

## 垃圾回收器

![image-20240329160441565](C:\Users\56511\AppData\Roaming\Typora\typora-user-images\image-20240329160441565.png)

### CMS

整个垃圾回收时间变长了，但是STW时间变短了，在垃圾回收过程中大部分时间用户线程还在执行，用户体验更好，但是吞吐量更低了（单位时间内执行的用户线程更少了）

初始标记、并发标记、重新标记、并发清理、并发重置

三色标记

增量更新

### G1

堆内存会分为2048个region，每个region的大小等于堆内存除以2048（Eden、Survivor、Old、Humongous：大对象）

如果一个对象超过了region的一半则称为大对象

初始标记、并发标记、最终标记、筛选回收

自定义STW时间

### ZGC

- 停顿时间不超过10ms；
- 停顿时间不会随着堆的大小，或者活跃对象的大小而增加；
- 支持8MB~4TB级别的堆（未来支持16TB）。



## 默认垃圾回收器

JDK5-JDK8：Parallel

JDK9-JDK21：G1

## 性能调优

JVM

## 编译器优化

## 执行模式

# JUC

## JUC概述

### JUC是什么

java.util.concurrent包的简称（java并发编程工具包），在Java1.5添加，目的就是为了更好的支持高并发任务。让开发者进行多线程编程时减少竞争条件和死锁的问题。

### 进程和线程的区别

进程：一个运行中的程序集合

线程：操作系统能够进行运算调度的最小单位

### 并发和并行的区别

并发(多线程操作同一个资源,交替执行)
CPU一核, 模拟出来多条线程,天下武功,唯快不破,快速交替
并行(多个人一起行走, 同时进行)
CPU多核,多个线程同时进行 ; 使用线程池操作

### 线程的五个状态

#### 操作系统层面

1. **新建** New
2. **就绪** Runnable
3. **运行** Running
4. **阻塞** Blocked
5. **死亡** Dead

#### Java层面

1. **新建** New
2. **运行** Runnable
3. **阻塞** Blocked
4. **等待** Waiting
5. **计时等待** Timed Waiting
6. **终止** Terminated

### wait/sleep的区别

1. 来自不同的类： wait来自object类, sleep来自线程类
2. 关于锁的释放：wait会释放锁, sleep不会释放锁
3. 使用的范围不同： wait必须在同步代码块中， sleep可以在任何地方睡眠

### 管程

Monitor 监视器，JVM中的同步基于进入和退出，使用管程对象实现的。

### 用户线程和守护线程

用户线程：自定义线程

守护线程：GC线程

主线程结束了，用户线程还没结束，jvm存活

没有用户线程了，只有守护线程，jvm结束

## Lock接口

- Lock和Synchronized的最大区别在于，synchronized是自动上锁和释放锁，Lock是需要用户手动上锁和释放锁。
- Lock是接口，synchronized是关键字
- Lock发生异常时，如果没有主动unLock()去释放锁，则很可能造成死锁的产生，因此需要在finally中释放锁。synchronized发生异常时，会自动释放锁，不会导致死锁的产生。
- Lock可以让等待锁的线程响应中断，而synchronized却不能够响应中断。
- Lock可以知道有没有成功获取到锁，synchronized不能知道
- Lock可以提高多线程进行读操作的效率

在性能上说，如果竞争资源不激烈，两者性能差不多。当竞争资源非常激烈时，Lock的性能要远优于synchronized。

ReentrantLock：可重入锁

Thread调用start方法时，不一定会马上创建线程，需要由操作系统决定（private **native** void start0();）

## 线程间通信

虚假唤醒问题：wait()在哪里睡就在哪里醒，判断条件用while循环中处理。

synchronized：

wait()等待

notifyAll()唤醒

Lock：

```java
private Lock lock = new ReentrantLock();

private Condition condition = lock.newCondition();
```

condition.await()等待

condition.signalAll()唤醒

## 线程间定制化通讯

创建多个Condition表示不同的队列，通过队列控制线程的等待和唤醒。

condition1.await()线程aa等待

condition2.signal()唤醒线程bb

condition2.await()线程bb等待

condition3.signal()唤醒线程cc

condition3.await()线程cc等待

condition1.signal()唤醒线程aa

```java
private int flag = 1;   // 1:aa 2:bb 3:cc
private Lock lock = new ReentrantLock();
private Condition condition1 = lock.newCondition(); // aa
private Condition condition2 = lock.newCondition(); // bb
private Condition condition3 = lock.newCondition(); // cc
```

## 集合的线程安全

### List集合线程不安全的问题

源码中add没有使用synchronized和Lock锁，所以是线程不安全的

处理ConcurrentModificationException并发修改异常：Vector、Collections、CopyOnWriteArrayList

Vector：类型中的add使用了synchronized关键字处理并发问题

```java
// Vector解决
List<String> list = new Vector();
```

Collections：在list的操作外包了一层synchronized关键字

```java
// Collections解决
List<String> list = Collections.synchronizedList(new ArrayList<>());
```

CopyOnWriteArrayList：写时复制技术

```java
// CopyOnWriteArrayList解决
List<String> list = new CopyOnWriteArrayList<>();
```

### HashSet线程不安全的问题

源码中add没有使用synchronized和Lock锁，所以是线程不安全的

处理ConcurrentModificationException并发修改异常：CopyOnWriteArraySet

```java
// CopyOnWriteArraySet解决
Set<String> set = new CopyOnWriteArraySet<>();
```

### HashMap线程不安全的问题

源码中put没有使用synchronized和Lock锁，所以是线程不安全的

处理ConcurrentModificationException并发修改异常：ConcurrentHashMap

```java
// ConcurrentHashMap解决
Map<String,String> map = new ConcurrentHashMap<>();
```

## 多线程锁

synchronized它修饰的对象有以下几种：
1. 修饰一个代码块，被修饰的代码块称为同步语句块，其作用的范围是大括号{}括起来的代码，作用的对象是调用这个代码块的对象；
2. 修饰一个普通方法，被修饰的方法称为同步方法，其作用的范围是整个方法，作用的对象是调用这个方法的对象；
3. 修饰一个静态的方法，其作用的范围是整个静态方法，作用的对象是这个类的所有对象；

### 公平锁和非公平锁

ReentrantLock构造函数传参（true公平锁：false非公平锁），无参默认非公平锁

```java
/**
 * Creates an instance of {@code ReentrantLock}.
 * This is equivalent to using {@code ReentrantLock(false)}.
 */
public ReentrantLock() {
    sync = new NonfairSync();
}

/**
 * Creates an instance of {@code ReentrantLock} with the
 * given fairness policy.
 *
 * @param fair {@code true} if this lock should use a fair ordering policy
 */
public ReentrantLock(boolean fair) {
    sync = fair ? new FairSync() : new NonfairSync();
}
```

非公平锁：会造成一个线程把活全干了，其他线程饿死（效率高）

公平锁：雨露均沾，所有线程全干活（效率低），检查当前线程是否有前驱节点，即是否其他线程正在等待获取锁

### 可重入锁

synchronized（隐式）和 Lock（显式）都是可重入锁，可递归调用

synchronized自动释放锁

Lock需要有上锁就要有解锁

### 死锁

#### 什么是死锁

是指多个线程在运行过程中因争夺资源而造成的一种相互等待的现象，如果没有外力干涉，他们就无法再继续执行下去。

#### 产生死锁的原因

1. 系统资源不足
2. 进程运行推进的顺序不当
3. 资源分配不当

#### 产生死锁的必要条件

1. 互斥条件：进程要求对所分配的资源进行排它性控制，即在一段时间内某资源仅为一进程所占用。

2. 请求和保持条件：当进程因请求资源而阻塞时，对已获得的资源保持不放。
3. 不剥夺条件：进程已获得的资源在未使用完之前，不能剥夺，只能在使用完时由自己释放。
4. 环路等待条件：在发生死锁时，必然存在一个进程--资源的环形链。

#### 解决死锁的办法



#### 验证是否是死锁

1. jps 类似Linux ps -ef
2. jstack jvm自带的跟踪工具

## 创建线程的方式

1. 继承Thread
2. 实现Runable
3. Callable接口
4. 线程池方式

## Callable

有返回值、会抛出异常、call()方法

FutrueTask()未来任务

run()运行线程

get()获取返回值，阻塞直到线程返回值

```java
FutureTask<Integer> futureTask2 = new FutureTask<>(() -> {
    return 1024;
});

new Thread(futureTask2, "AA").start();

while (!futureTask2.isDone()) {
    System.out.println("wait...");
}
System.out.println(futureTask2.get());
System.out.println("over");
```

## JUC的辅助类

### 减少计数CountDownLatch

可以使一个获多个线程等待其他线程各自执行完毕后再执行。

await()使当前线程阻塞，直到计数器值为0

countDown()计数器的值-1

可以解决那些一个或者多个线程在执行之前必须依赖于某些必要的前提业务先执行的场景。

```java
CountDownLatch countDownLatch = new CountDownLatch(6);
for (int i = 1; i <= 6; i++) {
    int finalI = new Random().nextInt(1,6);
    new Thread(() -> {
        try {
            TimeUnit.SECONDS.sleep(finalI);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + "号同学离开教室");
        countDownLatch.countDown();
    }, String.valueOf(i)).start();
}
countDownLatch.await();
System.out.println("班长锁门");
```

### 循环栅栏CyclicBarrier

可以实现一组线程相互等待，当所有线程都到达某个屏障点后再进行后续的操作。

await()当前线程等待，直到等待线程达到设置值就执行设置的线程。

```java
private static final int NUMBER = 7;

public static void main(String[] args) {
    CyclicBarrier cyclicBarrier = new CyclicBarrier(NUMBER, () -> {
        System.out.println("集齐7颗龙珠，召唤神龙");
    });
    for (int i = 1; i <= NUMBER; i++) {
        int finalI = new Random().nextInt(10);
        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(finalI);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName()+" 星龙珠被收集");
            try {
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        },String.valueOf(i)).start();
    }
}
```

### 信号量Semaphore

可以用来控制同时访问特定资源的线程数量，通过协调各个线程，以保证合理的使用资源。

acquire()获取令牌

release()释放令牌

new Semaphore(3)设置3各令牌

```java
public static void main(String[] args) {
    Semaphore semaphore = new Semaphore(3);
    for (int i = 1; i <= 6; i++){
        new Thread(()->{
            try {
                semaphore.acquire();
                System.out.println(Thread.currentThread().getName()+"号车进入停车场");
                TimeUnit.SECONDS.sleep(new Random().nextInt(5));
                System.out.println(Thread.currentThread().getName()+"号车------离开停车场");
                semaphore.release();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },String.valueOf(i)).start();
    }
}
```

## ReentrantReadWriteLock读写锁

### 乐观锁

乐观锁是一种基于数据版本控制的锁机制，它假设多个事务在同一时间对同一数据进行操作时，大部分情况下不会产生冲突。乐观锁的实现方式通常是在数据表中增加一个版本号或时间戳字段，当数据被读取时，记录下当前版本号或时间戳；当数据被更新时，检查当前版本号或时间戳是否与读取时的一致，如果不一致，则说明数据已被其他事务修改，当前事务执行失败。

### 悲观锁

悲观锁假设多个事务在同一时间对同一数据进行操作时很可能会产生冲突，因此在数据被访问前就先加锁，防止其他事务对数据进行修改。悲观锁的实现方式通常是在数据表中增加一个锁定字段，当数据被读取时，将该字段设置为锁定状态；当数据被更新时，检查该字段的状态，如果处于锁定状态，则说明数据已被其他事务修改，当前事务执行失败。

在操作资源前上锁，操作完成后解锁

只能一个一个操作，无法做到并发操作（效率低）

### 表锁

在操作表数据时，锁整个表

### 行锁

在操作行数据时，锁当前行

会发生死锁

### 读锁

共享锁，死锁

两个线程读同一条数据，并对数据进行修改时就会发生死锁的情况。

### 写锁

独占锁，死锁

两个线程分别写两条数据，恰好两个线程又同时写了对方的数据导致互相等待发生死锁问题。

### 读写锁

一个资源可以被多个读的线程访问，或被一个写的线程访问，但是不能同时存在读写线程，“读写互斥、读读共享”。

```java
class MyCache {
    private volatile Map<String, Object> map = new HashMap<>();

    private ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public void put(String key, Object value) {
        rwLock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "正在写入" + key);
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            map.put(key, value);
            System.out.println(Thread.currentThread().getName() + "写入完成");
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public Object get(String key) {
        rwLock.readLock().lock();
        try {
            Object result = null;
            System.out.println(Thread.currentThread().getName() + "正在读取" + key);
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            result = map.get(key);
            System.out.println(Thread.currentThread().getName() + "读取完成");
            return result;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            rwLock.readLock().unlock();
        }
    }
}
```

### 读写锁的演变

1. 无锁，多线程抢占资源
2. 添加锁，synchronized和ReentrantLock都是独占锁，每次只能来一个操作，缺点：不能共享
3. 读写锁，ReentrantReadWriteLock，读读共享，提升性能，同时多人进行读操作，缺点：1.容易造成锁饥饿，一直读没有写操作，2.读时不能进行写操作，写操作时可以读。

### 锁降级

写入锁降级为读锁：获取写锁，获取读锁，释放写锁，释放读锁

```java
ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
ReentrantReadWriteLock.ReadLock readLock = rwLock.readLock();
ReentrantReadWriteLock.WriteLock writeLock = rwLock.writeLock();

// 获取写锁
writeLock.lock();
System.out.println("wuming");

// 获取读锁
readLock.lock();
System.out.println("------read");

// 释放写锁
writeLock.unlock();

// 释放读锁
readLock.unlock();
```

## BlockingQueue阻塞队列

### 概述

队列：先进先出

当队列为空时，从队列获取元素的操作将会阻塞

当队列为满时，向队列添加元素的操作将会阻塞

在某些情况下会自动挂起线程，一旦条件满足，被挂起的线程又会自动被唤醒

好处：不需要关注什么时候阻塞线程，什么时候唤醒线程

### 阻塞队列的分类

#### **ArrayBlockingQueue**

基于数组的有界阻塞队列

#### **LinkedBlockingQueue**

基于链表的有界阻塞队列

#### DelayQueue

使用优先队列实现的延迟无界阻塞队列

#### PriorityBlockingQueue

支持优先级排序的无界阻塞队列

#### SyschronousQueue

单元素队列

#### LinkedTransferQueue

由链表组成的无界阻塞队列

#### LinkedBlockintDeque

由链表组成的双向阻塞队列

### 阻塞队列的常用方法

| 方法类型 | 抛出异常  | 特殊值   | 阻塞   | 超时               |
| -------- | --------- | -------- | ------ | ------------------ |
| 插入     | add(e)    | offer(e) | put(e) | offer(e,time,unit) |
| 移除     | remove()  | poll()   | take() | poll(time,unit)    |
| 检查     | element() | peek()   | 不可用 | 不可以             |

## ThreadPool线程池

### 线程池的使用

#### 1.Executors.newFixedThreadPool(int)

一池n线程

#### 2.Executors.newSingleThreadExecutor()

一池一线程

#### 3.Executors.newCachedThreadPool()

一池可扩容线程

全部由ThreadPoolExecutor创建

int corePoolSize,	常驻线程数量
int maximumPoolSize,		最大线程数量
long keepAliveTime,		线程存活时间
TimeUnit unit,		时间单位
BlockingQueue<Runnable> workQueue,		工作阻塞队列
ThreadFactory threadFactory,		线程工厂
RejectedExecutionHandler handler		拒绝策略

### 线程池的工作流程

常驻线程 =》 阻塞队列 =》 创建新线程 =》 拒绝策略

### 拒绝策略

AbortPolicy默认：抛出异常

CallerRunsPolicy：调用者运行，不抛弃任务，也不抛出异常，将任务退回到调用者

DiscardOldestPolicy：抛弃队列中等待最久的任务，然后把当前任务加入队列中尝试再次提交当前任务

DiscardPolicy：默默丢掉无法处理的任务，不做任何处理也不抛出异常

### 自定义线程池

```java
ExecutorService threadPool = new ThreadPoolExecutor(
        2,
        5,
        10,
        TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(3),
        Executors.defaultThreadFactory(),
        new ThreadPoolExecutor.AbortPolicy()
);
```

## Fork/Join分支合并框架

是以递归方式将可以并行的任务拆分成更小的任务，然后将每个子任务的结果合并起来生成整体结果

## CompletableFuture异步回调



# MYSQL

## 事务特性

原子性：事务由原子的操作序列组成，要么全部成功，要么全部失败

一致性：一个事务在执行前和执行后数据库都必须保持一致性状态，在进行多表操作时，多个表要么都是事务更新后的值，要么都是事务更新前的值

隔离性：多个用户操作数据时不能被其他事务干扰，多个并发事务之间要相互隔离

持久性：事务一旦提交成功，对数据库中的数据改变就是永久性的

## 事务并发问题与隔离级别

![img](https://pic3.zhimg.com/80/v2-18be2609265e48b95c071c794115b00e_720w.webp)

### 事务并发问题

- **脏读：**脏读是指在一个事务处理过程里读取了另一个未提交的事务中的数据，例如，账户A转帐给B500元，B余额增加后但事务还没有提交完成，此时如果另外的请求中获取的是B增加后的余额，这就发生了脏读，因为事务如果失败回滚时，B的余额就不应该增加。
- **不可重复读：**不可重复读是指对于数据库中某个数据，一个事务范围内多次查询返回了不同的数据值，这是由于在多次查询之间，有其他事务修改了数据并进行了提交。
- **幻读：**是指一个事务中执行两次完全相同的查询时，第二次查询所返回的结果集跟第一个查询不相同。与不可重复读的区别在于，不可重复读是对同一条记录，两次读取的值不同。而幻读是记录的增加或删除，导致两次相同条件获取的结果记录数不同。

### 隔离级别

- **级别1读未提交：**可以读取到其他事务未提交的内容，这是最低的隔离级别，这个隔离级别下，前面提到的三种并发问题都有可能发生。
- **级别2读已提交：**只能读取到其他事务已经提交的数据。这个隔离级别可以解决脏读问题。
- **级别三可重复读：**保证整个事务过程中，对同数据的多次读取结果是相同的。这个级别可以解决脏读和不可重复读的问题。MySQL默认的隔离级别就是可重复读。
- **级别四串行化：**最高的隔离级别，所有事务操作都依次顺序执行。这个级别会导致并发度下降，性能最差。不过这个级别可以解决前面提到的所有并发问题。

## 事务分类

- **扁平化事务**：在扁平事务中，所有的操作都在同一层次，这也是我们平时使用最多的一种事务。它的主要限制是不能提交或者回滚事务的某一部分，要么都成功，要么都回滚。
- **带保存点的扁平事务：**为了解决第一种事务的弊端，就有了第二种带保存点的扁平事务。它允许事务在执行过程中回滚到较早的状态，而不是全部回滚。通过在事务中插入保存点，当操作失败后，可以选择回滚到最近的保存点处。
- **链事务：**可以看做是第二种事务的变种。它在事务提交时，会将必要的上下文隐式传递给下一个事务，当事务失败时就可以回滚到最近的事务。不过，链事务只能回滚到最近的保存点，而带保存点的扁平化事务是可以回滚到任意的保存点。
- **嵌套事务:**由顶层事务和子事务构成，类似于树的结构。一般顶层事务负责逻辑管理，子事务负责具体的工作，子事务可以提交，但真正提交要等到父事务提交，如果上层事务回滚，那么所有的子事务都会回滚。
- **分布式事务：**是指分布式环境中的扁平化事务。
