<?php

define('APP_START_TIME', microtime(true));

function println($datas, $result = true, $exit = true)
{
    static $lasttime = APP_START_TIME;

    $thistime = microtime(true);
    $usedtime = $thistime - $lasttime;
    $lasttime = $thistime;
    $usedtime = sprintf("% 7d ms] ", $usedtime * 1000);

    $memory = memory_get_usage() / 1000000;
    $memory = sprintf("% 6.1f MB ", $memory);

    $message = date('[m-d H:i:s ');
    $message .= $memory . $usedtime;

    if (is_array($datas) && !empty($datas)) {
        $message .= '[';
        $message .= implode('||', $datas);
        $message .= '] ';
    } else {
        $message .= $datas;
    }

    $message .= $result ? '[SUCCESS]' : '[FAILED]';

    echo $message;
    echo PHP_EOL;
    if ($exit) {
        exit;
    }
}

function request($url, $post = null, $timeout = 40, $sendcookie = true, $options = array())
{
    $ch = curl_init($url);
    curl_setopt($ch, CURLOPT_HEADER, 0);
    curl_setopt($ch, CURLOPT_USERAGENT, 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:48.0) Gecko/20100101 Firefox/48.0');
    curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 35);
    curl_setopt($ch, CURLOPT_TIMEOUT, $timeout ? $timeout : 40);
    if ($sendcookie) {
        $cookie = '';
        foreach ($_COOKIE as $key => $val) {
            $cookie .= rawurlencode($key) . '=' . rawurlencode($val) . ';';
        }
        curl_setopt($ch, CURLOPT_COOKIE, $cookie);
    }
    if ($post) {
        curl_setopt($ch, CURLOPT_POST, 1);
        curl_setopt($ch, CURLOPT_POSTFIELDS, is_array($post) ? http_build_query($post) : $post);
    }

    if (!ini_get('safe_mode') && ini_get('open_basedir') == '') {
        curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 1);
    }
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);

    curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, 0);

    foreach ($options as $key => $value) {
        curl_setopt($ch, $key, $value);
    }

    $ret = curl_exec($ch);
    $httpcode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    $content_length = curl_getinfo($ch, CURLINFO_CONTENT_LENGTH_DOWNLOAD);
    if (!$content_length) $content_length = curl_getinfo($ch, CURLINFO_SIZE_DOWNLOAD);
    $content_type = curl_getinfo($ch, CURLINFO_CONTENT_TYPE);

    curl_close($ch);

    return array(
        'httpcode'       => $httpcode,
        'content_length' => $content_length,
        'content_type'   => $content_type,
        'content'        => $ret
    );
}