package czz.study.service;

import czz.study.domain.RedPacket;

public interface RedPacketService {

    /**
     * 获取红包信息
     *
     * @param id 红包id
     * @return 红包具体信息
     */
    RedPacket getRedPacket(Long id);

    /**
     * 扣减抢红包数
     *
     * @param id 红包id
     * @return 更新记录条数
     */
    int decreaseRedPacket(Long id);

    /**
     * 扣减抢红包数
     *
     * @param id 红包id
     * @param version 版本号
     * @return 结果
     */
    int decreaseRedPacketForVersion(Long id, Integer version);
}
