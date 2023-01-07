const bigImg = document.getElementById("img-main");
const smallImgs = document.getElementsByClassName("img-others");
const bigImgId = parseInt(bigImg.getAttribute("src").substring(16));

for(let i = 0; i < smallImgs.length; i++){
    if (parseInt(smallImgs[i].getAttribute("id")) != bigImgId){
        smallImgs[i].setAttribute("style", "border: none")
    }else{
        smallImgs[0].setAttribute("style", "border: 5px solid red")
    }
}
function changeMainImg(imgIndex){
    const smallImg = document.getElementById(imgIndex.toString());
    const bigImg = document.getElementById("img-main");
    const smallImgs = document.getElementsByClassName("img-others");

    bigImg.setAttribute("src","/image/display/" + imgIndex.toString())
    /*if(parseInt(smallImg.getAttribute("id")) == imgIndex){
        smallImg.setAttribute("style", "border: 5px solid red")
    }*/
    for(let i = 0; i < smallImgs.length; i++){
        if (parseInt(smallImgs[i].getAttribute("id")) != imgIndex){
            smallImgs[i].setAttribute("style", "border: none")
        }else{
            smallImg.setAttribute("style", "border: 5px solid red")
        }
    }
}