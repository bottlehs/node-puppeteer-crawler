const axios = require("axios");
const cheerio = require("cheerio");
const fs = require("fs");

let html = "";
let page = 1000;

async function getHtml(url) {
  try {
    return await axios.get(url);
  } catch (error) {
    console.error(error);
  }
}

async function getRec() {
  html = await getHtml(`https://www.op.gg/ranking/ladder/page=${page}`);

  // console.log(html.data);

  const $ = cheerio.load(html.data);
  let rec = {};
  let items = [];
  $(".ranking-table__cell--summoner span").each(async function(index, elem) {
      // console.log(elem);
      let summonerName = $(elem).text();
      items.push(summonerName);
    });


    console.log('=====결과====');
    console.log(items);

    items.forEach(async item => {
      // await insertExample(item);
    });

    fs.writeFileSync(`opgg_ranking/page.${page}.json`,JSON.stringify(items));

    console.log(rec);

    if ( page < 2000 ) {
      page++;
      const time = Math.floor(Math.random() * 1000) + 500;
      console.log(time);
      setTimeout(() => {
        getRec();        
      }, time);
    }
}

getRec();