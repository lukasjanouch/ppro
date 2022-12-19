function addInputLine() {
    const input = document.createElement("input");// Create an <input> node
    const count = document.getElementById("add-album-div").childElementCount;

    input.setAttribute("type", "file");
    input.setAttribute("class", "img-input");
    input.setAttribute("th:field", "*{images[__${itemStat.index}__].image}");

    document.getElementById("add-album-div").appendChild(input);// Append it to the parent
    //var count = document.getElementById("add-album-div").childElementCount;
}