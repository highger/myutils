package com.example.demo.applicationcontext;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public enum ChannelCodeEnum {


    WAN("G111", "xx"),
    WANG("G222", "yy"),
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
