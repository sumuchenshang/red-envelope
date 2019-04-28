package czz.study.domain;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 红包类
 *
 * @author chenzhengzhou
 * @version 1.0
 * @date 2019/4/16 9:55
 */
/*
CREATE TABLE `r_red_packet` (
  `id` int(12) NOT NULL AUTO_INCREMENT,
  `user_id` int(12) NOT NULL,
  `amount` decimal(16,2) NOT NULL,
  `send_date` timestamp NULL DEFAULT NULL,
  `total` int(12) NOT NULL,
  `unit_amount` decimal(16,0) NOT NULL,
  `stock` int(12) DEFAULT NULL,
  `version` int(12) NOT NULL DEFAULT '0',
  `note` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
 */
@Data
public class RedPacket implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private Double amount;

    private Timestamp sendDate;

    private Integer total;

    private Double unitAmount;

    private Integer stock;

    private Integer version;

    private String note;


}
