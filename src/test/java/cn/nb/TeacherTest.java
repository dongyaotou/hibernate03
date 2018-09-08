package cn.nb;

import cn.nb.bean.Teacher;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TeacherTest {
    Transaction transaction=null;
    Session session=null;
    @Before
    public  void before(){
        //01.读取核心配置文件 configure()底层就是加载了/hibernate.cfg.xml
        Configuration configuration=new Configuration().configure();
        //02.创建会话工厂 sessionFactory
        SessionFactory factory= configuration.buildSessionFactory();
        //03.创建会话  session
        session=factory.openSession();
        //04.开启事务
        transaction = session.beginTransaction();
    }


    @After
    public  void after(){

        //08.关闭会话
        session.close();
    }


@Test
    public void addTeacher(){
        //最终显示的是查询语句
    Teacher teacher=new Teacher(1,"李连杰");//这是一种瞬时状态
    session.save(teacher);//这是一种持久状态
    session.evict(teacher);//这是一种游离状态
    Teacher teacher1= (Teacher) session.get(Teacher.class,1);
    System.out.println(teacher1);

}
@Test
    public void  addTeacher2(){
    //最后是一条insert的sql语句
    //这三种状态是相对于session说的
    Teacher teacher=new Teacher(4,"李连杰");//这是一种瞬时状态   注意这里的主键不能是数据库中已经存在的主键的序号
   //s teacher.setName("老师5");//转变为瞬时态
    session.save(teacher);  //持久态开始 才被session管理
    System.out.println("*****************************");
    transaction.commit();//最后必须手动提交事务




}
@Test
    public void addTeacher3(){
    Teacher  teacher=new Teacher(5,"老师4"); //瞬时态  insert
    session.save(teacher);  //持久态
    teacher.setName("赵永强哈哈");//update
    System.out.println("*************************");
    transaction.commit();





}

@Test
    public void addTeacher4(){
        //最终的运行结果只有一条update的语句
    Teacher  teacher=new Teacher(6,"朱军"); //瞬时态
    session.save(teacher);  //持久态
    teacher.setName("妞妞");
    teacher.setName("嗖嗖");
    teacher.setName("豆豆");
    System.out.println("**************");
    System.out.println(teacher);
    transaction.commit();//没有这句话是没有hql语句输出的，同时在数据库中也不会插入相应的记录
}


@Test
    public void addTeacher5(){
    Teacher o = (Teacher) session.get(Teacher.class, 1);//持久化状态
    o.setName("奥黛丽赫本");
    /*因为teacher已经是持久状态了，所以不需要save和update*/
    transaction.commit();
}
    @Test
    public void test06(){
        Teacher teacher=new Teacher(7,"习近平");
        //即根据Id判断是调用save或者update方法，Id咋在数据库不存在时调用save，Id在数据库存在时调用update。
        session.saveOrUpdate(teacher);//持久状态
        transaction.commit();

    }
    /**
     * saveOrUpdate（）
     * 验证二：
     *    数据在数据库存在，
     *    saveOrUpdate（） 01.先产生select语句根据id查询
     *                   如果修改了对象的属性
     *                   02.产生update语句
     */
    @Test
    public void test07(){
        Teacher  teacher=new Teacher(4,"老师哈哈哈"); //瞬时态
        session.saveOrUpdate(teacher); //持久态
        System.out.println("***************************");
        transaction.commit();
    }



    /**
     *   saveOrUpdate（）：
     *     数据库中没有对应的数据，
     *     然后更改了数据的属性！
     *     01.select
     *     02.insert
     *     03.update
     */
    @Test
    public void test08(){
        /*执行的过程
        * 01.查询  没有   select
        * 02.插入   insert
        * 03.更新   update
        *
        * */

        Teacher  teacher=new Teacher(9,"niuniuniu"); //瞬时态
        session.saveOrUpdate(teacher); //持久态
        teacher.setName("老师5");
        System.out.println("**************************");
        transaction.commit();
    }
    /**
     *   merge
     *   验证一：
     *   数据库没有对应的数据
     *     01.select语句
     *     02.insert
     */

    @Test
    public void test09(){
        Teacher  teacher=new Teacher(4,"老师4"); //瞬时态
        session.merge(teacher);
        transaction.commit();
    }
//注意这种是不会报错的，但是在数据库中不会插入新的数据，必须使用session会话，
    //这段程序没有产生session会话，所以没有办法与数据库建立联系
    @Test
    public void test011(){
        Teacher  teacher=new Teacher(10,"中关村"); //瞬时态

        transaction.commit();
    }
    /**
     *   merge
     *   验证二：
     *   数据库有对应的数据
     *       只有一条select语句
     */
    @Test
    public void test10(){
        Teacher  teacher=new Teacher(4,"老师帅"); //瞬时态
        session.merge(teacher);
        transaction.commit();
    }

    /**
     *   merge
     *   验证三：
     *   数据库有对应的数据，但是对数据进行修改
     *       只有一条select语句
     *       01.select
     *       02.update
     */
    @Test
    public void test11(){
        Teacher  teacher=new Teacher(4,"老师帅"); //瞬时态
        session.merge(teacher);
        transaction.commit();
    }
    /**
     *   merge
     *   验证四：
     *   数据库没有对应的数据，创建对象之后 对数据进行修改
     *       01.select
     *       02.insert
     */
    @Test
    public void test12(){
Teacher teacher =new Teacher(10,"小升阳");
session.merge(teacher);
transaction.commit();
    }
    /**
     *   merge
     *   验证五：
     *     不会改变对象的状态
     *
     * 在事务的最后执行SQL时，session缓存里面有多个（>1）主键一样的对象。所以就会报错
     *
     *
     *
     */
    @Test
    public void test13(){
        Teacher  teacher=new Teacher(4,"老师帅"); //瞬时态
        session.merge(teacher);  //不会改变对象的状态
        teacher.setName("老师5");  //瞬时态
        session.update(teacher); //报错
        teacher.setName("老");
        transaction.commit();
    }


    /**
     *
     * 瞬时态和游离态的区别就是是否拥有OID！
     * OID怎么来的？只要曾经被session管理过的对象都有OID！
     *
     *  save():   把瞬时态转换成持久态
     *  update(): 把游离态转换成持久态
     *  saveOrUpdate():
     *         会根据对象是否有OID来判断执行save还是update
     *           如果有oid  执行update   就相当于有序号就能执行更新操作
     *           如果没有oid  执行save    就相当于没有序号就只有先保存到session会话中
     *  merge()： 产生的sql语句和saveOrUpdate有点类似，
     *            但是！！！！！
     *            01.merge不会改变对象的状态
     *            02.当我们的对象处于瞬时状态时，会将对象复制一份到session的缓存中，
     *              然后执行save方法，执行insert
     */

    @Test
    public void test14(){
        Teacher  teacher=new Teacher(11,"老师帅");//瞬时状态
        session.save(teacher);  //持久态
        session.evict(teacher);  // 游离态
        session.update(teacher);  // 持久态
        transaction.commit();
    }

    /**
     * 如果 数据在数据库中不存在  这里自己还是没有做出效果
     */
    @Test
    public void test15(){
        Teacher  teacher=new Teacher(2,"老师5");//瞬时状态
        session.merge(teacher); //瞬时状态
        session.evict(teacher);// 把持久化状态转换成游离态   瞬时状态
        session.update(teacher);// 把游离态转换成持久化状态  瞬时状态
        transaction.commit();
    }
    /**
     *  数据在数据库中存在！会把id  转换成 oid 让这个对象变成游离态
     *  只会产生一条hibernate语句
     */
    @Test
    public void test16(){
        Teacher  teacher=new Teacher(4,"哈哈哈");//游离态
        session.update(teacher); // 把游离态转换成持久化状态   update语句
        transaction.commit();
    }

    @Test
    public   void  test17(){
        Teacher  teacher=new Teacher(5,"哈哈哈55");//瞬时状态
        session.save(teacher); // 把瞬时状态转换成持久化状态   insert语句
        transaction.commit();
    }

    /**
     *  数据在数据库中不存在！
     */
    @Test
    public   void  test18(){
        Teacher  teacher=new Teacher(4,"哈哈哈");//瞬时状态
        session.update(teacher); // 把游离态转换成持久化状态     报错
        transaction.commit();
    }

    @Test
    public   void  test19(){
        Teacher  teacher=new Teacher(3,"哈哈哈");//游离状态
        session.update(teacher); // 把游离态转换成持久化状态
        transaction.commit();
    }

    /**
     * commit 和flush的区别
     * 相同点：
     *     都会同步到数据库！
     * 不同点：
     *   commit:提交数据到数据库，会永久保存
     *   flush: 暂时保存，不一定会持久化！
     *
     *   commit在执行的时候，默认回执行flush操作，
     *   在执行flush的时候会清理缓存，
     *   清理缓存的时候执行脏检查！
     *
     *   脏检查：
     *      在我们的对象被session管理的时候，
     *      session会在缓存中创建对象的一个副本（快照）来保存对象现在的一种状态！
     *      在清理缓存的时候会拿现在的对象状态和之前的副本进行比较，
     *      如果现在的对象属性发生了变化，这个对象就是脏对象！
     *      flush会把脏对象同步到数据库。
     *      如果没有commit，数据只是暂时的保存在数据库中！
     *      之后commit才能永久保存！
     */
    @Test
    public void  test20(){
        //从数据库中获取id为1的老师信息
        Teacher teacher= (Teacher) session.get(Teacher.class,1); //持久态
        teacher.setName("小黑");  //如果删除这条语句 就不会出现update  证明没有脏对象
        System.out.println("**********************");
        session.flush();  //产生了update语句  证明同步到了数据库
    }


    @Test
    public void  test21(){
        //从数据库中获取id为1的老师信息
        Teacher teacher= (Teacher) session.get(Teacher.class,1); //持久态
        teacher.setName("小黑");  //如果删除这条语句 就不会出现update  证明没有脏对象
        System.out.println("**********************");
        session.flush();  //产生了update语句  证明同步到了数据库
        session.evict(teacher);  //清除指定对象 证明我们的数据不是来自缓存
        Teacher teacher2= (Teacher) session.get(Teacher.class,1); //持久态
        System.out.println(teacher2.getName());
    }


}
