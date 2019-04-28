package czz.study.factory;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;

/**
 * 从 XML 中构建 SqlSessionFactory，并使用单例设计模式，防止工厂被多次创建,同时可以通过getSession方法获取SqlSession。
 *
 * @author chenzhengzhou
 * @version 1.0
 * @date 2019/3/25 9:21
 */
public class MybatisFactory {

    static {
        try {
            instance = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("conf.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static SqlSessionFactory instance;

    /**
     * 通过SqlSessionFactory获取SqlSession
     *
     * @return SqlSession
     */
    public static SqlSession getSqlSession() {

        return instance.openSession();
    }

}
