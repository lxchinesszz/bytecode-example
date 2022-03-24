package com.example.test.javassist;

import javassist.*;

import java.lang.reflect.Method;

/**
 * @author liuxin
 * 2022/3/24 11:29 PM
 */
public class JavassistTest {

    public static void main(String[] args) throws Exception {
        modifyMethod();
//        addMethod();
//        addClass();
    }

    public static void addClass() throws Exception {
        // 类库池, jvm中所加载的class
        ClassPool pool = ClassPool.getDefault();
        // 创建一个学校类
        CtClass schoolClass = pool.makeClass("com.example.test.School");
        // 设置为公有类
        schoolClass.setModifiers(Modifier.PUBLIC);
        // 获取String类型
        CtClass stringClass = pool.get("java.lang.String");
        // 获取list类型
        CtClass listClass = pool.get("java.util.List");
        // 获取学生的类型
        CtClass userClass = pool.get("com.example.test.javassist.User");
        // 给学校添加一个校名属性
        CtField nameField = new CtField(stringClass, "schoolName", schoolClass);
        nameField.setModifiers(Modifier.PUBLIC);
        schoolClass.addField(nameField);
        // 给学校添加一个学生集合
        CtField studentList = new CtField(listClass, "users", schoolClass);
        studentList.setModifiers(Modifier.PUBLIC);
        schoolClass.addField(studentList);
        // 给学校一个空构造
        CtConstructor ctConstructor = CtNewConstructor.make("public School() " +
                "{this.schoolName=\"湖畔小学\";this.users = new java.util.ArrayList();}", schoolClass);
        schoolClass.addConstructor(ctConstructor);

        // 给学校一个addUser的方法
        CtMethod m = new CtMethod(CtClass.voidType, "addUser", new CtClass[]{userClass}, schoolClass);
        m.setModifiers(Modifier.PUBLIC);
        // 添加学生对象到students属性中, $1代表参数1
        m.setBody("this.users.add($1);");
        schoolClass.addMethod(m);

        // 给学校添加一个介绍的方法
        CtMethod introduce = new CtMethod(CtClass.voidType, "introduce", new CtClass[]{}, schoolClass);
        introduce.setBody("System.out.println(\"The School name is \" + this.schoolName);");
        introduce.insertAfter("System.out.println(this.users);");
        schoolClass.addMethod(introduce);

        // 加载修改后的学校
        Class<?> schoolLoadClass = schoolClass.toClass();
        // 构建一个学校(空构造)
        Object school = schoolLoadClass.newInstance();
        // 获取添加用户方法
        Method addUserMethod = schoolLoadClass.getDeclaredMethod("addUser", userClass.toClass());
        addUserMethod.invoke(school, new User("小明"));
        addUserMethod.invoke(school, new User("小张"));
        // 获取介绍方法，把刚才的信息给打印处理
        Method introduceMethod = school.getClass().getDeclaredMethod("introduce");
        introduceMethod.invoke(school);
    }

    public static void addMethod() throws Exception {
        // 类库池, jvm中所加载的class
        ClassPool pool = ClassPool.getDefault();
        // 获取指定的User类
        CtClass ctClass = pool.get("com.example.test.javassist.User");
        // 增加方法
        CtMethod ctMethod = new CtMethod(CtClass.intType, "getAgeSum",
                new CtClass[]{CtClass.intType, CtClass.intType}, ctClass);
        // 设置方法的访问修饰
        ctMethod.setModifiers(Modifier.PUBLIC);
        // 设置方法体代码
        ctMethod.setBody("return $1 + $2;");
        // 添加新建的方法到原有的类中
        ctClass.addMethod(ctMethod);
        // 加载修改后的类
        ctClass.toClass();
        // 创建对象
        User stu = new User();
        // 获取calc方法
        Method dMethod = User.class.getDeclaredMethod("getAgeSum", new Class[]
                {int.class, int.class});
        // 反射调用 方法
        Object result = dMethod.invoke(stu, 10, 20);
        System.out.println(result);
    }

    public static void modifyMethod() throws Exception {
        // 类库池, jvm中所加载的class
        ClassPool pool = ClassPool.getDefault();
        // 获取指定的Student类
        CtClass ctClass = pool.get("com.example.test.javassist.User");
        // 获取sayHello方法
        CtMethod ctMethod = ctClass.getDeclaredMethod("sayJavassist");
        // 在方法的代码后追加 一段代码
        ctMethod.insertAfter("System.out.println(\"I'm Javassist.\");");
        // 使用当前的ClassLoader加载被修改后的类
        Class<?> newClass = ctClass.toClass();
        User user = (User) newClass.newInstance();
        System.out.println(user.sayJavassist());
    }
}

