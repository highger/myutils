package com.example.demo.applicationcontext;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public enum ChannelCodeEnum {

    //万向
    WANG_XIANG("G10001", "万向"),
    //网金社
    WANG_JIN_SHE("G30002", "网金社"),
    //无
    NONE("000000", "无");
    private final String channelCode;

    private final String agencyName;

    public static ChannelCodeEnum getChannelCodeEnum(String channelCode) {
        for (ChannelCodeEnum channelCodeEnum : ChannelCodeEnum.values()) {
            if (channelCodeEnum.getChannelCode().equals(channelCode)) {
                return channelCodeEnum;
            }
        }
        return NONE;
    }

    public static ChannelCodeEnum valueOfAgencyName(String agencyName) {
        for (ChannelCodeEnum channelCodeEnum : ChannelCodeEnum.values()) {
            if (channelCodeEnum.getAgencyName().equals(agencyName)) {
                return channelCodeEnum;
            }
        }
        return NONE;
    }

    public static Optional<String> getAgencyName(String channelCode) {
        ChannelCodeEnum channelCodeEnum = ChannelCodeEnum.getChannelCodeEnum(channelCode);
        return ChannelCodeEnum.NONE == channelCodeEnum ?
            Optional.empty() : Optional.of(channelCodeEnum.getAgencyName());
    }
}
