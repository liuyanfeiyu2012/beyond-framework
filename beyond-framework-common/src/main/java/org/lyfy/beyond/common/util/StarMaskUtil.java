package org.lyfy.beyond.common.util;

import org.lyfy.beyond.common.constant.Constants;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class StarMaskUtil {

    /**
     * 地址，单位等字符串信息加星号
     * 屏蔽规则为： 每4个汉字展示前1位，末3位屏蔽；少于4个汉字的只展示第1位，其他屏蔽。（数字字母均视同汉字处理，emoji算一个字符）
     *
     * @param commonString
     * @return
     */
    public static final String starMaskCommonString(String commonString) {
        if (StringUtils.isBlank(commonString)) {
            return commonString;
        }

        String s = StringUtils.strip(commonString);
        List<String> groupEmojiCharStrList = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c == '\ud83c' || c == '\ud83d') && i + 1 < s.length()) {
                char nextC = s.charAt(i + 1);
                if ('\udc00' <= nextC && '\udfff' >= nextC) {
                    groupEmojiCharStrList.add(String.valueOf(c) + String.valueOf(nextC));
                    i++;
                    continue;
                }
            }

            groupEmojiCharStrList.add(String.valueOf(c));
        }

        List<String> unmaskedStrList = new ArrayList<>();
        for (int i = 0; i < groupEmojiCharStrList.size(); i += 4) {
            unmaskedStrList.add(groupEmojiCharStrList.get(i));
        }

        String maskResult = StringUtils.join(unmaskedStrList, StringUtils.repeat(Constants.STAR, 3));
        return maskResult + StringUtils.repeat(Constants.STAR, groupEmojiCharStrList.size() + 3 - 4 * unmaskedStrList.size());
    }

    private static final Set<Character> alphabetCharSet = Arrays.stream(ArrayUtils.toObject(
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray())).collect(Collectors.toSet());

    /**
     * 姓名加星号
     * 屏蔽规则为：
     * 含中文姓名：每4个汉字展示前1位，末3位屏蔽；少于4个汉字的只展示第1位， 其他屏蔽。（数字母均视同汉处理）
     * 例子：张 **
     * 全英文姓名：如果中间是有空格的，屏蔽第一个空格后面所字母；如果中间没有空格，末3分之2( 四舍五入)部分屏蔽掉。
     * 例子： Mark Elliot Zuckerberg 屏蔽后为 Mark ****** ********** ；
     * Mark 屏蔽后为 M***；
     *
     * @param name
     * @return
     */
    public static final String starMaskName(String name) {
        if (StringUtils.isBlank(name)) {
            return name;
        }

        String s = StringUtils.strip(name);
        final int firstIndexOfSpace = StringUtils.indexOf(s, StringUtils.SPACE);
        final int firstIndexOfFullSpace = StringUtils.indexOf(s, Constants.FULL_SPACE);
        if (0 < firstIndexOfSpace || 0 < firstIndexOfFullSpace) {
            final int minIndex = IntStream.of(firstIndexOfSpace, firstIndexOfFullSpace).filter(i -> 0 < i).min().orElse(0);
            String firstPartBeforeSpace = StringUtils.substring(s, 0, minIndex);
            if (!Stream.of(firstPartBeforeSpace.toCharArray()).allMatch(alphabetCharSet::contains)) {
                firstPartBeforeSpace = starMaskCommonString(firstPartBeforeSpace);
            }

            StringBuilder sb = new StringBuilder(firstPartBeforeSpace);
            for (int i = minIndex; i < s.length(); i++) {
                String c = String.valueOf(s.charAt(i));
                if (StringUtils.SPACE.equals(c) || Constants.FULL_SPACE.equals(c)) {
                    sb.append(c);
                } else {
                    sb.append(Constants.STAR);
                }
            }
            return sb.toString();
        } else {
            return starMaskCommonString(name);
        }
    }

    /**
     * 手机号码加星号
     * 屏蔽规则为：11位手机号码，展示为: 号码前3位 + ***** + 号码末3位
     * 例子： 186*****258
     *
     * @param cellPhone
     * @return
     */
    public static final String starMaskCellphone(String cellPhone) {
        if (StringUtils.isBlank(cellPhone)) {
            return cellPhone;
        }

        String s = StringUtils.strip(cellPhone);
        if (6 >= s.length()) {
            return s;
        }

        return StringUtils.substring(s, 0, 3)
                + StringUtils.join(IntStream.range(0, s.length() - 6).boxed().map(i -> Constants.STAR).toArray())
                + StringUtils.substring(s, s.length() - 3);
    }

    /**
     * 银行卡号加星号
     * 屏蔽规则为：只展示前2位、末2位，中间均屏蔽。
     *
     * @param bankCard
     * @return
     */
    public static final String starMaskBankCard(String bankCard) {
        if (StringUtils.isBlank(bankCard)) {
            return bankCard;
        }

        String s = StringUtils.strip(bankCard);
        if (4 >= s.length()) {
            return s;
        }

        return StringUtils.substring(s, 0, 2)
                + StringUtils.join(IntStream.range(0, s.length() - 4).boxed().map(i -> Constants.STAR).toArray())
                + StringUtils.substring(s, s.length() - 2);
    }

    /**
     * 身份证号加星号
     * 屏蔽规则为：
     * 号码长度为15位的：号码第7位到12位和最后1位屏蔽，其他不屏蔽
     * 号码长度为18位的：号码第7位到14位和最后2位屏蔽，其他不屏蔽
     * 例子 : 510281******30* :
     * 例子 : 510281********30**
     *
     * @param idCard
     * @return
     */
    public static final String starMaskIdCard(String idCard) {
        if (StringUtils.isBlank(idCard)) {
            return idCard;
        }

        String s = StringUtils.strip(idCard);
        int middleStarLength = 0;
        String tailStarStr = null;
        if (15 == s.length()) {
            middleStarLength = 6;
            tailStarStr = Constants.STAR;
        } else if (18 == s.length()) {
            middleStarLength = 8;
            tailStarStr = StringUtils.repeat(Constants.STAR, 2);
        } else {
            return s;
        }

        return StringUtils.substring(s, 0, 6)
                + StringUtils.join(IntStream.range(0, middleStarLength).boxed().map(i -> Constants.STAR).toArray())
                + StringUtils.substring(s, 6 + middleStarLength, 8 + middleStarLength) + tailStarStr;
    }

    /**
     * 电子邮箱加星号
     * 屏蔽规则为：Email地址‘@’前面字符的末3分之2(四舍五入)部分屏蔽掉。
     * 例子：ag****@gmail.com
     *
     * @param email
     * @return
     */
    public static final String starMaskEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return email;
        }

        String s = StringUtils.strip(email);
        int indexOfAt = StringUtils.indexOf(s, Constants.AT);
        if (0 >= indexOfAt) {
            return s;
        }

        int unmaskHeadLength = (indexOfAt + 1) / 3;
        return StringUtils.substring(s, 0, unmaskHeadLength)
                + StringUtils.join(IntStream.range(0, indexOfAt - unmaskHeadLength).boxed().map(i -> Constants.STAR).toArray())
                + StringUtils.substring(s, indexOfAt);
    }

    /**
     * 固定电话加星号
     * 屏蔽规则为：区号不屏蔽；电话号码屏蔽末4位；分机号码屏蔽末2位。
     * 例子：0571 -8700**** 8700**** 8700****-00** 00**00**
     *
     * @param tel
     * @return
     */
    public static final String starMaskTel(String tel) {
        if (StringUtils.isBlank(tel)) {
            return tel;
        }

        String s = StringUtils.strip(tel);
        if (4 >= s.length()) {
            return s;
        }

        String twoStarStr = StringUtils.repeat(Constants.STAR, 2);
        String fourStarStr = StringUtils.repeat(Constants.STAR, 4);

        int hyphenQty = StringUtils.countMatches(s, Constants.HYPHEN);
        if (0 == hyphenQty) {
            return StringUtils.substring(s, 0, s.length() - 4) + fourStarStr;
        } else {
            final int firstIndexOfHyphen = StringUtils.indexOf(s, Constants.HYPHEN);
            if (1 == hyphenQty) {
                if (4 < firstIndexOfHyphen) {
                    final String left = StringUtils.substring(s, 0, firstIndexOfHyphen - 4) + fourStarStr;
                    if (firstIndexOfHyphen + 2 > s.length()) {
                        return left + StringUtils.substring(s, firstIndexOfHyphen);
                    } else {
                        return left + StringUtils.substring(s, firstIndexOfHyphen, s.length() - 2) + twoStarStr;
                    }
                } else {
                    return StringUtils.substring(s, 0, s.length() - 4) + fourStarStr;
                }
            } else if (2 == hyphenQty) {
                final int lastIndexOfHyphen = StringUtils.lastIndexOf(s, Constants.HYPHEN);
                String right = null;
                if (lastIndexOfHyphen + 2 < s.length()) {
                    right = StringUtils.substring(s, lastIndexOfHyphen, s.length() - 2) + twoStarStr;
                } else {
                    right = StringUtils.substring(s, lastIndexOfHyphen);
                }

                if (4 >= lastIndexOfHyphen - firstIndexOfHyphen) {
                    return StringUtils.substring(s, 0, lastIndexOfHyphen) + right;
                } else {
                    return StringUtils.substring(s, 0, lastIndexOfHyphen - 4) + StringUtils.repeat(Constants.STAR, 4) + right;
                }
            } else {
                return s;
            }
        }
    }
}

