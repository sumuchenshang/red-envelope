package czz.study.domain;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 用户红包类
 *
 * @author chenzhengzhou
 * @version 1.0
 * @date 2019/4/16 9:54
 */
/*
DROP  TABLE IF EXISTS r_user_red_packet ;
CREATE TABLE `r_user_red_packet` (
  `id` int(12) NOT NULL AUTO_INCREMENT,
  `user_id` int(12) DEFAULT NULL,
  `amount` decimal(16,2) DEFAULT NULL,
  `red_packet_id` int(12) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `grab_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 */
@Data
public class UserRedPacket implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long redPacketId;

    private Long userId;

    private Double amount;

    private Timestamp grabTime;

    private String note;

}
