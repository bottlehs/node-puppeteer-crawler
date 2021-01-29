const dotenv = require("dotenv");
const puppeteer = require("puppeteer");

// dotenv
dotenv.config();

(async () => {
  /**
   * 웹 브라우저를 실행 한다.
   */
  const browser = await puppeteer.launch();

  /**
   * 웹 브라우저 페이지를 생성한다.
   */
  const page = await browser.newPage();
  
  /**
   * 네이버 아이디,비밀번호 계정 셋팅
   */
  const naver_id = process.env.NAVER_ID;
  const naver_pw = process.env.NAVER_PW;
  const naver_query = process.env.NAVER_QUERY;
  console.log(process.env.NODE_ENV)
  console.log(naver_id);


  /**
   * 네이버(로그인전) 메인 화면으로 이동
   */  
  await page.goto("https://naver.com");

  /**
   * 네이버(로그인전) 메인화면 스크립캡처
   */
  await page.screenshot({ path: "naver_1.png", fullPage: true });  

  /**
   * 네이버 로그인 화면으로 이동
   */
  await page.goto("https://nid.naver.com/nidlogin.login?mode=form&url=https%3A%2F%2Fwww.naver.com");

  /**
   * 네이버 아이디, 비밀번호 input 을 찾아 입력
   */
  await page.evaluate(
    (id, pw) => {
      document.querySelector("#id").value = id;
      document.querySelector("#pw").value = pw;
    },
    naver_id,
    naver_pw
  );

  /**
   * 네이버(로그인전) 로그인 화면 스크립캡처
   */
  await page.screenshot({ path: "naver_2.png", fullPage: true });  

  /**
   * 네이버 로그인 버튼 클릭
   */
  await page.click(".btn_global");
  await page.waitForNavigation();

  /**
   * 네이버(로그인후) 메인 화면으로 이동
   */  
  await page.goto("https://naver.com");

  /**
   * 네이버(로그인후) 메인화면 스크립캡처
   */
  await page.screenshot({ path: "naver_3.png", fullPage: true });

  /**
   * 검색어 입력
   */

  /*
  await page.evaluate(
    (query) => {
      document.querySelector("#query").value = query;
    },
    naver_query
  );
  */

  /**
   * 네이버 검색 버튼 클릭
   */
  
  /*
  await page.click("#search_btn");
  await page.waitForNavigation();
  */

  /**
   * 네이버(로그인후) 검색 결과 화면으로 이동
   */  
  await page.goto("https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=%EB%B8%94%EB%A3%A8%EB%B2%A0%EB%A6%AC");
  /**
   * 네이버(로그인후) 검색 결과 화면 스크립캡처
   */
  await page.screenshot({ path: "naver_4.png", fullPage: true });

  /**
   * 네이버(로그인후) 검색 결과 상세화면으로 이동
   */  
  await page.goto("https://smartstore.naver.com/delightgarden/products/680927812?n_media=27758&n_query=%EB%B8%94%EB%A3%A8%EB%B2%A0%EB%A6%AC&n_rank=1&n_ad_group=grp-a001-02-000000019529302&n_ad=nad-a001-02-000000121085945&n_campaign_type=2&n_mall_pid=680927812&n_ad_group_type=2&NaPm=ct%3Dkkhvyzgw%7Cci%3D0zC0002%2DDvbuIPiQi1on%7Ctr%3Dpla%7Chk%3D5ae4083c32a2c63dfe720f2d7c5218ea77f188af");
  /**
   * 네이버(로그인후) 검색 결과 상세화면 스크립캡처
   */
  await page.screenshot({ path: "naver_5.png", fullPage: true });


  await browser.close();
})();
