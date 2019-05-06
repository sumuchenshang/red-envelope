package czz.study.service.impl;

import czz.study.dao.RedPacketDao;
import czz.study.dao.UserRedPacketDao;
import czz.study.domain.RedPacket;
import czz.study.domain.UserRedPacket;
import czz.study.service.UserRedPacketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRedPacketServiceImpl implements UserRedPacketService {

    public UserRedPacketServiceImpl(UserRedPacketDao userRedPacketDao, RedPacketDao redPacketDao) {
        this.userRedPacketDao = userRedPacketDao;
        this.redPacketDao = redPacketDao;
    }

    private UserRedPacketDao userRedPacketDao;

    private RedPacketDao redPacketDao;

    private static final int FAILED = 0;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int grapRedPacket(Long redPacketId, Long userId) {

        //用不同方式抢红包，
        //普通抢红包方式 1000人抢950个红包 3037毫秒，超发0个 ，2658秒，超发1个，2905秒 超发10个，有时候不会出现超发现象
        //但是当数据库请求的线程数变多或者下面延迟变大，超发现象就会变得严重
        return grapRedPacketForCommon(redPacketId, userId);
        //悲观锁抢红包方式  1000人抢950个红包 2890毫秒，3059毫秒，2708毫秒，很奇怪，按道理悲观锁的速度是不快的，
        //我觉得可能的原因有两点：①mysql优化了锁同步的性能，②我这种抢乐观锁方式导致CAS自旋的概率变得很大，从而浪费更多的CPU资源，效率低于synchronized。
        // return grapRedPacketForUpdate(redPacketId, userId);
        //乐观锁抢红包方式  1000人抢950个红包 2806毫秒，剩余79个，3006毫秒，剩余82个，2761秒，125个
        // return grapRedPacketForVersion(redPacketId, userId);
        //乐观锁重入抢红包根据时间戳（100毫秒）重入 3232毫秒，剩余257个，2982毫秒，252个，3116秒，252个
        // return grapRedPacketForVersionDate(redPacketId, userId);
        //乐观锁重入抢红包根据次数（3次）重入 3151毫秒，剩余185个，3039毫秒，180个，2833秒，264个
        // return grapRedPacketForVersionTime(redPacketId, userId);

    }

    /**
     * 普通抢红包，会产生超发现象
     *
     * @param redPacketId 红包id
     * @param userId 用户id
     * @return 结果
     */
    private int grapRedPacketForCommon(Long redPacketId, Long userId) {
        //获取红包信息，用该方式容易造成超发（发出的红包多余设置）
        RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
        //下面加延时是为了运行结果更加明显，不加的话不容易出现超发现象，值越大，超发现象越明显，但是悲观锁更容易死锁
        // try {
        //     Thread.sleep(1);
        // } catch (Exception e) {
        //
        // }
        //当前小红包库存大于0
        if (redPacket.getStock() > 0) {
            //不用乐观锁的普通减数量
            redPacketDao.decreaseRedPacket(redPacketId);
            UserRedPacket userRedPacket = new UserRedPacket();
            userRedPacket.setRedPacketId(redPacketId);
            userRedPacket.setUserId(userId);
            Double amount = new Double(10);
            userRedPacket.setAmount(amount);
            userRedPacket.setNote("抢红包" + redPacketId);
            return userRedPacketDao.grapRedPacket(userRedPacket);
        }
        return FAILED;
    }

    /**
     * 悲观锁抢红包，正常抢，但是速度慢
     *
     * @param redPacketId 红包id
     * @param userId 用户id
     * @return 结果
     */
    private int grapRedPacketForUpdate(Long redPacketId, Long userId) {

        //获取红包信息，利用for update查询（悲观锁），不会超发，但是可能会因为for update造成死锁（还未解决）
        RedPacket redPacket = redPacketDao.getRedPacketForUpdate(redPacketId);
        //下面加延时是为了运行结果更加明显，值越大，悲观锁越容易死锁
        // try {
        //     Thread.sleep(1);
        // } catch (Exception e) {
        //
        // }
        //当前小红包库存大于0
        if (redPacket.getStock() > 0) {
            //不用乐观锁的普通减数量
            redPacketDao.decreaseRedPacket(redPacketId);

            UserRedPacket userRedPacket = new UserRedPacket();
            userRedPacket.setRedPacketId(redPacketId);
            userRedPacket.setUserId(userId);
            Double amount = new Double(10);
            userRedPacket.setAmount(amount);
            userRedPacket.setNote("抢红包" + redPacketId);
            return userRedPacketDao.grapRedPacket(userRedPacket);
        }
        return FAILED;
    }

    /**
     * 根据乐观锁抢红包，可能会发生红包没抢完现象
     *
     * @param redPacketId 红包id
     * @param userId 用户id
     * @return 结果
     */
    private int grapRedPacketForVersion(Long redPacketId, Long userId) {
        //1获取红包信息
        RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
        //下面加延时是为了运行结果更加明显，值越大，剩余红包数越多，
        // try {
        //     Thread.sleep(1);
        // } catch (Exception e) {
        //
        // }
        //当前小红包库存大于0
        if (redPacket.getStock() > 0) {
            //使用乐观锁
            int update = redPacketDao.decreaseRedPacketForVersion(redPacketId, redPacket.getVersion());
            //如果没有数据更新，则说明其他线程已经修改过数据，重新抢夺
            if (update == 0) {
                return FAILED;
            }
            UserRedPacket userRedPacket = new UserRedPacket();
            userRedPacket.setRedPacketId(redPacketId);
            userRedPacket.setUserId(userId);
            Double amount = new Double(10);
            userRedPacket.setAmount(amount);
            userRedPacket.setNote("抢红包" + redPacketId);
            return userRedPacketDao.grapRedPacket(userRedPacket);
        }
        return FAILED;
    }

    /**
     * 根据乐观锁抢红包,使用时间戳重入抢红包
     *
     * @param redPacketId 红包id
     * @param userId 用户id
     * @return 结果
     */
    private int grapRedPacketForVersionDate(Long redPacketId, Long userId) {
        long start = System.currentTimeMillis();

        while (true) {
            long end = System.currentTimeMillis();
            if (end - start > 1000) {
                return FAILED;
            }
            //1获取红包信息
            RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
            //下面加延时是为了运行结果更加明显，值越大，剩余红包数越多，建议不要加，加了会导致剩余红包数比不使用重入更多
            // try {
            //     Thread.sleep(1);
            // } catch (Exception e) {
            //
            // }
            //当前小红包库存大于0
            if (redPacket.getStock() > 0) {
                //使用乐观锁
                int update = redPacketDao.decreaseRedPacketForVersion(redPacketId, redPacket.getVersion());
                //如果没有数据更新，则说明其他线程已经修改过数据，重新抢夺
                if (update == 0) {
                    continue;
                }
                UserRedPacket userRedPacket = new UserRedPacket();
                userRedPacket.setRedPacketId(redPacketId);
                userRedPacket.setUserId(userId);
                Double amount = new Double(10);
                userRedPacket.setAmount(amount);
                userRedPacket.setNote("抢红包" + redPacketId);
                return userRedPacketDao.grapRedPacket(userRedPacket);
            }
            return FAILED;
        }
    }

    /**
     * 根据乐观锁抢红包,使用次数重入抢红包
     *
     * @param redPacketId 红包id
     * @param userId 用户id
     * @return 结果
     */
    private int grapRedPacketForVersionTime(Long redPacketId, Long userId) {
        for (int i = 0; i < 3; i++) {
            //1获取红包信息
            RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
            //下面加延时是为了运行结果更加明显，值越大，剩余红包数越多，建议不要加，加了会导致剩余红包数比不使用重入更多

            //当前小红包库存大于0
            if (redPacket.getStock() > 0) {
                //使用乐观锁
                int update = redPacketDao.decreaseRedPacketForVersion(redPacketId, redPacket.getVersion());
                //如果没有数据更新，则说明其他线程已经修改过数据，重新抢夺
                if (update == 0) {
                    continue;
                }
                UserRedPacket userRedPacket = new UserRedPacket();
                userRedPacket.setRedPacketId(redPacketId);
                userRedPacket.setUserId(userId);
                Double amount = new Double(10);
                userRedPacket.setAmount(amount);
                userRedPacket.setNote("抢红包" + redPacketId);
                return userRedPacketDao.grapRedPacket(userRedPacket);
            } else {
                return FAILED;

            }
        }
        return FAILED;
    }

}
