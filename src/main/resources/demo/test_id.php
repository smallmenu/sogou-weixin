<?php
/**
 * PHP 模拟提交查询 with Cookie
 */

include('function.php');

$url = 'http://weixin.sogou.com/weixin?type=1&s_from=input&query=%E4%B8%AD%E5%9B%BD&ie=utf8&_sug_=n&_sug_type_=';

$snuidUrl = 'http://localhost:9999/take';

for ($i = 0; $i < 50; $i++) {

    if ($i % 10 == 0) {
        $api = request($snuidUrl);
        if ($api['httpcode'] == 200) {
            $apiContent = $api['content'];
            $apiData = json_decode($apiContent, true);

            if ($apiData['state']) {
                $suv = $apiData['data']['suv'];
                $snuid = $apiData['data']['snuid'];
            } else {
                exit('api error');
            }
        } else {
            exit('api error');
        }
    }

    println(array('开始请求第'. $i), true, false);

    $options = array(
        CURLOPT_COOKIE => 'sct=1;SUV=' . $suv . ';SNUID=' . $snuid,
    );
    $result = request($url, null, 3, false, $options);

    if ($result['httpcode'] == 200) {
        $content = $result['content'];

        if (strpos($content, '请输入验证码') === false) {
            println(array('请求成功'), true, false);

            $lists = array();
            preg_match_all('#<!-- a -->([\s\S]*)<!-- z -->#U', $content, $listContent);

            foreach ($listContent[1] as $key => $list) {

                preg_match('#<p class="tit">([\s\S]*)</p>#U', $list, $weixinname);
                preg_match('#<label name="em_weixinhao">(.*)</label>#U', $list, $weixinhao);
                preg_match('#" d="(.*)"#U', $list, $weixinid);
                preg_match('#认证：</dt>([\s\S]*)</dd>#U', $list, $weixinauth);

                if (!empty($weixinname[1]) && !empty($weixinhao[1]) && !empty($weixinid)) {
                    $weixin = array();
                    $weixin['name'] = trim(strip_tags($weixinname[1]));
                    $weixin['openid'] = trim($weixinid[1]);
                    $weixin['uid'] = trim($weixinhao[1]);
                    $weixin['auth'] = trim(strip_tags($weixinauth[1]));

                    $lists[] = $weixin;
                }
            }
            print_r($lists);
        } else {
            println(array('请输入验证码'), true, false);
        }
    } else {
        println(array('请求失败'), true, false);
    }
    sleep(1);
}






