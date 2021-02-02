const dotenv = require("dotenv");
const puppeteer = require("puppeteer");

// dotenv
dotenv.config();

(async () => {
  /**
   * 웹 브라우저를 실행 한다.
   */
  const browser = await puppeteer.launch({
    // headless: false
  });

  /**
   * 웹 브라우저 페이지를 생성한다.
   */
  const page = await browser.newPage();
  
  /**
   * 네이버 아이디,비밀번호 계정 셋팅
   */
  const naver_query = process.env.NAVER_QUERY;

  /**
   * 네이버(로그인전) 메인 화면으로 이동
   */  
  await page.goto("https://naver.com", {
    waitUntil: 'load',
    timeout: 0,
  });

  /**
   * 검색어 입력
   */

  await page.evaluate(
    (query) => {
      document.querySelector("#query").value = query;
    },
    naver_query
  );

  await page.screenshot({ path: "naver_1.png", fullPage: true });  

  /**
   * 네이버 검색 버튼 클릭
   */
  console.log('elHandleArray 0');  
  await page.click(".btn_submit");
  await page.waitFor('.link_tit')
  
   const texts = await page.$$eval('a.link_tit', divs => divs.map(({ innerText }) => innerText));
   console.log(texts);

   await page.screenshot({ path: "naver_2.png", fullPage: true });

   const example = await page.$$('a.link_tit');
   console.log(example[0].textContent);
   console.log(example.length);
   await example[0].click();

   const pages = await browser.pages();
   /*
   let i = 0;
   pages.forEach(newPage => {
    await newPage.screenshot({ path: "naver_newPage_"+i+"_.png", fullPage: true });
    i++;
    newPage.close();
   });
   */

   console.log(pages);
   console.log(pages.length);

  await browser.close(); 
})();
