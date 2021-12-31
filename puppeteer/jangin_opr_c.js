const axios = require("axios");
const fs = require("fs");

let html = "";
let page = 500;
let max_page = 750;
let items = [];
let complteItems = [];

async function getJson() {
  try {
    if ( page < max_page ) {
      const temp = fs.readFileSync(`opgg_ranking/page.${page}.json`,'utf-8');
      const summoners = JSON.parse(temp);
      summoners.forEach(summoner => {
        items.push(summoner);
      });
    
      getSummoner();
    } else {
      console.log('종료');      
    }
  } catch {
    console.log('종료');
  }

}

async function apiGetCall(url) {
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
    const summonerResponse = await apiGetCall(`http://api.jangin.io/api/summoner/${encodeURI(summonerName)}`);
    let id = 0;
    if ( summonerResponse ) {
      if ( summonerResponse.status == 200 ) {
        summonerResponseStatus = summonerResponse.status;
        /*
        id = summonerResponse.data.id;
        const gameResponse = await apiPutCall(`http://api.jangin.io/api/game/summoner/${id}`);
        if ( gameResponse ) {
          gameResponseStatus = gameResponse.status
        }
        */
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


getJson();
