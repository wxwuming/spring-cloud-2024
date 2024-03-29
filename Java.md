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

新生代：默认占1/3，Eden：伊甸区、S0：Survivor0、S1：Survivor1，占比8：1：1

老年代：默认占2/3

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
