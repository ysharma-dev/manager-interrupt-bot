const parseGoogleSheet = () => {
    return new Promise((resolve, reject) => {
        const https = require('https');
        let url = process.env.GOOGLE_SHEET_URL;
        let constructedObject = {};
        //TODO - Make SlackIDs 12-factor compliant by using an env variable
        const slackIDs = {
            "User1": "AAAAAAAAAAA",
            "User2": "BBBBBBBBBBB",
            "User3": "CCCCCCCCCCC",
            "User4": "DDDDDDDDDDD",
            "User5": "EEEEEEEEEEE"
        }
        
        https.get(url, (response) => {
            let body = "";

            response.on("data", (chunk) => {
                body += chunk;
            });
            
            response.on("end", () => {
                try {
                    json = JSON.parse(body);
                    let objArray = json.feed.entry;
                    for(let i=0; i<objArray.length; i++){
                        if(objArray[i].gs$cell.row > 1 && objArray[i].gs$cell.col == 1){
                            constructedObject[objArray[i].gs$cell.inputValue] = {
                                "interruptName": objArray[i+1].gs$cell.inputValue,
                                "slackID": slackIDs[objArray[i+1].gs$cell.inputValue]
                            };
                        }
                    }
                    resolve(constructedObject);
                } catch (error){
                    console.error(error.message);
                }
            });
        }).on("error", (error) => {
            console.error(error.message);
            reject(error.message);
        }); 
    });
}

module.exports = {
    parseGoogleSheet
}