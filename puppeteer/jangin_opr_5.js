const axios = require("axios");
const { NodeWithChildren } = require("cheerio");
const fs = require("fs");

let html = "";
let page = 5;
let max_page = 6;
let items = [];
let complteItems = [];
let startDate = "2022-01-07";
let endDate = "2022-01-07";
let limitEndDate = "";

function calcDate() {
  let today = new Date();
  let year = today.getFullYear(); // 년도
  let month = today.getMonth() + 1;  // 월
  let date = today.getDate();  // 날짜

  if ( month < 10 ) {
    month = `0${month}`;
  }

  if ( date < 10 ) {
    date = `0${date}`;    
  }

  limitEndDate = `${year}-${month}-${date}`;  

  let date1 = new Date(startDate);
  let date2 = new Date(endDate);
  console.log(`체크 : ${startDate} / ${endDate}`);
  if ( date1.getTime() == date2.getTime() ) {
    console.log('동일');
    // 동일
    date2.setDate(date1.getDate() + 1);    
    startDate = `${date1.getFullYear()}-${date1.getMonth() + 1}-${date1.getDate()}`;    
    endDate = `${date2.getFullYear()}-${date2.getMonth() + 1}-${date2.getDate()}`;    
  } else {
    // end 크다
    console.log('end 크다');    
    date2.setDate(date2.getDate() + 1);
    date1 = date2;
    startDate = `${date1.getFullYear()}-${date1.getMonth() + 1}-${date1.getDate()}`;
    date2.setDate(date1.getDate() + 1);
    endDate = `${date2.getFullYear()}-${date2.getMonth() + 1}-${date2.getDate()}`;

  };

  console.log(`날짜는 이렇게.. : ${startDate} ~ ${endDate}`);
}

async function getJson() {
  try {
    if ( page < max_page ) {
      const temp = fs.readFileSync(`opgg_ranking/${page}.json`,'utf-8');
      const summoners = JSON.parse(temp);
      summoners.forEach(summoner => {
        items.push(summoner);
      });
    
      getSummoner();
    } else {
      // 날짜 체크
      let date1 = new Date(endDate);
      var date2 = new Date(limitEndDate);
      console.log(`날짜 체크 A : ${date1} / ${date1.getTime()}`);
      console.log(`날짜 체크 B : ${date2} / ${date2.getTime()}`);

      if ( date2.getTime() <= date1.getTime() ) {
        console.log('종료 1');
      } else {
        console.log('날짜 변경후 재시작');
        page = 5;
        calcDate();
        getJson();
      }
    }
  } catch (e) {
    console.log('종료 2');
    console.log(e);
  }

}

async function apiGetCall(url) {
  console.log(url)
  try {
    return await axios.get(url);
  } catch (error) {
    console.error(error);
  }
}

async function apiPutCall(url) {
  try {
    return await axios.put(url);
  } catch (error) {
    console.error(error);
  }
}


async function getSummoner() {
  if ( items.length == 0 ) {
    if ( complteItems.length != 0 ) {
      fs.writeFileSync(`opgg_ranking_complete_opr/page.${page}.json`,JSON.stringify(complteItems));
      complteItems = [];
    }

    page++;
    getJson();
  } else {
    const summonerName = items[0];
    items.shift();

    let summonerResponseStatus = 0;
    let gameResponseStatus = 0;
    console.log(summonerName);

    let paramsStartDate = startDate.split('-');
    let paramsEndDate = endDate.split('-');

    if ( paramsStartDate[0] < 10 ) {
      paramsStartDate[0] = `0${paramsStartDate[0]}`;
    }
    if ( paramsStartDate[1] < 10 ) {
      paramsStartDate[1] = `0${paramsStartDate[1]}`;
    }
    if ( paramsStartDate[2] < 10 ) {
      paramsStartDate[2] = `0${paramsStartDate[2]}`;
    }

    if ( paramsEndDate[0] < 10 ) {
      paramsEndDate[0] = `0${paramsEndDate[0]}`;
    }
    if ( paramsEndDate[1] < 10 ) {
      paramsEndDate[1] = `0${paramsEndDate[1]}`;
    }
    if ( paramsEndDate[2] < 10 ) {
      paramsEndDate[2] = `0${paramsEndDate[2]}`;
    }    

    paramsStartDate = `${paramsStartDate[0]}-${paramsStartDate[1]}-${paramsStartDate[2]}`;
    paramsEndDate = `${paramsEndDate[0]}-${paramsEndDate[1]}-${paramsEndDate[2]}`;

    console.log(`요청 : paramsStartDate:${paramsStartDate} / paramsEndDate:${paramsEndDate}`);

    const summonerResponse = await apiGetCall(`https://api.jangin.io/api/summoner/${encodeURI(summonerName)}`);
    let id = 0;
    if ( summonerResponse ) {
      if ( summonerResponse.status == 200 ) {
        console.log(`요청 정상 : paramsStartDate:${paramsStartDate} / paramsEndDate:${paramsEndDate}`);
        summonerResponseStatus = summonerResponse.status;
        id = summonerResponse.data.id;
        const gameResponse = await apiPutCall(`https://api.jangin.io/api/game/data/summoner/${id}/${paramsStartDate}/${paramsEndDate}`);
        if ( gameResponse ) {
          gameResponseStatus = gameResponse.status
        }
      }
    }

    console.log(`${summonerName}_page_${page}_data_${new Date()}`);
    complteItems.push({
      id: id,
      naem: summonerName,
      date: new Date(),
      summonerStatus: summonerResponseStatus,
      gameStatus: gameResponseStatus
    });

    const time = Math.floor(Math.random() * 1000) + 500;
    console.log(time);
    setTimeout(() => {
      getSummoner();  
    }, time);
  }
}

calcDate();

getJson();
