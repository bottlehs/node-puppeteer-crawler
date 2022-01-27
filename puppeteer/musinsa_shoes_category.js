const axios = require("axios");
const cheerio = require("cheerio");
const fs = require("fs");

let html = "";
let page = 1;

async function getHtml(url) {
  try {
    return await axios.get(url);
  } catch (error) {
    console.error(error);
  }
}

async function getRec() {
  html = await getHtml(`https://www.musinsa.com/category/005`);

  const $ = cheerio.load(html.data);
  let rec = {};
  let items = [];
  $("#category_2depth_list .list_division_brand dd ul li a").each(async function(index, elem) {
    let category = $(elem).attr('data-value');
    console.log(category);
    const params = {
      name: category,
      type: ""
    }
    await axios.post("http://api.onofffit.nextinnovation.kr/api/v1/admin/category", params, {
      headers: {
        "Content-Type": "application/json",
        "X-AUTH-TOKEN": "Bearer " + "eyJhbGciOiJzaGEyNTYiLCJ0eXAiOiJKV1QifS57InVzZXJfaWQiOjI0OTczNzU1OCwiaWF0IjoxNjQxNzc4OTM3fS5lNWNmYWI2MGEyMDBjMTNmZDA4NTkxZWI1MDI4OWMyYThjYTFiZGNmMjZiNjU3MWFmMzAwYjU2NWEzODYyMGQ3"
      }
    });
  });
}

getRec();