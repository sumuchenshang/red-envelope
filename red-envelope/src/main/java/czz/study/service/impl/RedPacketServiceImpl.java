package czz.study.service.impl;

import czz.study.dao.RedPacketDao;
import czz.study.domain.RedPacket;
import czz.study.service.RedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RedPacketServiceImpl implements RedPacketService {

    @Autowired
    private RedPacketDao redPacketDao = null;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public RedPacket getRedPacket(Long id) {

        return redPacketDao.getRedPacket(id);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int decreaseRedPacket(Long id) {

        return redPacketDao.decreaseRedPacket(id);
    }

    @Override
    public int decreaseRedPacketForVersion(Long id, Integer version) {
        return redPacketDao.decreaseRedPacketForVersion(id, version);
    }

}
