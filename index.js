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
  const shop_name = process.env.SHOP_NAME;
  const shop_birthday = process.env.SHOP_BIRTHDAY;
  const shop_hp = process.env.SHOP_HP;
  console.log(process.env.NODE_ENV)
  console.log(shop_name);


  /**
   * 쇼핑엔티(로그인전) 비회원구매
   */  
  await page.goto("https://www.shoppingntmall.com/member/nm-order/auth");

  /**
   * 쇼핑엔티(로그인전) 비회원구매 스크립캡처
   */
  await page.screenshot({ path: "shoppingntmall_1.png", fullPage: true });  

  https://www.shoppingntmall.com/member/nm-order/auth
  /**
   * 쇼핑엔티 이름, 생년월일, 전화번호 input 을 찾아 입력
   */
  await page.evaluate(
    (name, birthday, hp) => {
      document.querySelector("#name").value = name;
      document.querySelector("#birthday").value = birthday;
      document.querySelector("#hp").value = hp;      

      document.querySelector("#agree0").checked = true;
      document.querySelector("#agree1").checked = true;
      document.querySelector("#agree4").checked = true;
    },
    shop_name,
    shop_birthday,
    shop_hp    
  );

  /**
   * 쇼핑엔티(로그인전) 로그인 화면 스크립캡처
   */
  await page.screenshot({ path: "shoppingntmall_2.png", fullPage: true });  

  /**
   * 쇼핑엔티(로그인전) 로그인 버튼 클릭
   */
  await page.click('.join_form > .btn_wrap button');
  await page.on('response', response => {    
    console.log('ok')
  });

  // await page.waitForNavigation();  

  /**
   * 쇼핑엔티(로그인후) 상품구매 화면으로 이동
   */  
  await page.goto("https://www.shoppingntmall.com/order/purchase/8004085");

  /**
   * 쇼핑엔티(로그인전) 로그인 화면 스크립캡처
   */
  await page.screenshot({ path: "shoppingntmall_3.png", fullPage: true });    

  // await page.waitForNavigation();


  await browser.close();
})();
