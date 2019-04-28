package czz.study.dao;

import czz.study.domain.UserRedPacket;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRedPacketDao {

    /**
     * 保存抢红包信息
     *
     * @param userRedPacket 抢红包信息
     * @return 插入条数
     */
    int grapRedPacket(@Param("userRedPacket")UserRedPacket userRedPacket);
}
