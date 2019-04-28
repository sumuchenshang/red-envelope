package czz.study.dao;

import czz.study.domain.RedPacket;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RedPacketDao {

    /**
     * 获取红包信息
     *
     * @param id 红包id
     * @return 红包具体信息
     */
    RedPacket getRedPacket(@Param("id")Long id);

    /**
     * 获取红包信息
     *
     * @param id 红包id
     * @return 红包具体信息
     */
    RedPacket getRedPacketForUpdate(@Param("id") Long id);

    /**
     * 扣减抢红包数
     *
     * @param id 红包id
     * @return 更新记录条数
     */
    int decreaseRedPacket(@Param("id")Long id);

    /**
     * 扣减抢红包数
     *
     * @param id 红包id
     * @return 结果
     */
    int decreaseRedPacketForVersion(@Param("id")Long id,@Param("version")Integer version);
}
