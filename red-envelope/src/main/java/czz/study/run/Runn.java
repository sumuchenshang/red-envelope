package czz.study.run;

import czz.study.App;
import czz.study.dao.RedPacketDao;
import czz.study.dao.UserRedPacketDao;
import czz.study.factory.MybatisFactory;
import czz.study.service.UserRedPacketService;
import czz.study.service.impl.UserRedPacketServiceImpl;
import org.apache.ibatis.session.SqlSession;

public class Runn implements Runnable {

    SqlSession sqlSession;
    public Runn() {
        sqlSession= MybatisFactory.getSqlSession();
        RedPacketDao redPacketDao = sqlSession.getMapper(RedPacketDao.class);
        UserRedPacketDao userRedPacketDao = sqlSession.getMapper(UserRedPacketDao.class);
        this.userRedPacketService = new UserRedPacketServiceImpl(userRedPacketDao,redPacketDao);
    }

    private UserRedPacketService userRedPacketService;

    @Override
    public void run() {

        System.out.println("线程" + Thread.currentThread().getName() + "准备啦开始抢了");
        try {
            App.cdOrder.await(); //战士们都处于等待命令状态
            System.out.println("线程" + Thread.currentThread().getName() + "开始抢了");
            String userIdString = Thread.currentThread().getName();
            userIdString = userIdString.substring(14);
            Long userId = new Long(userIdString);
            userRedPacketService.grapRedPacket(1L, userId);
            sqlSession.commit();
            System.out.println("线程" + Thread.currentThread().getName() + "这个人抢完了");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
            App.cdAnswer.countDown(); //任务运行完成，返回给指挥官，cdAnswer减1。
        }
    }
}
