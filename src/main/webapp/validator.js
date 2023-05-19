function isNotValidCategory(category) {
    if (category.val() === "all") {
        alert("카테고리를 선택해주세요.");
        category.focus();
        return true;
    }

    return false;
}

function isNotValidWriter(writer) {
    if (writer.val().length < 3 || 4 < writer.val().length) {
        alert("작성자는 3글자 이상, 5글자 미만입니다.");
        writer.focus();
        return;
    }

    return false;
}

function isNotValidTypePassword(passwordFirst) {
    const regex = /[a-zA-Z0-9{}\[\]\/?.,;:|()*~`!^\-_+<>@#$%&\\='"]{4,15}/;
    if (!regex.test(passwordFirst)) {
        alert("비밀번호는 4글자 이상, 16글자 미만, 영문/숫자/특수문자 만 가능합니다.");
        return true;
    }

    return false;
}

function isNotValidPassword(passwordFirst, passwordSecond) {
    if (isNotValidTypePassword(passwordFirst.val())) {
        passwordFirst.focus();
        return true;
    }

    if (passwordFirst.val() !== passwordSecond.val()) {
        alert("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        passwordSecond.focus();
        return true;
    }

    return false;
}

function isNotValidTitle(title) {
    if (title.val().length < 3 || 100 < title.val().length) {
        alert("제목은 4글자 이상, 100글자 미만입니다.");
        title.focus();
        return true;
    }

    return false;
}

function isNotValidContent(content) {
    if (content.val().length < 4 || 2000 < content.val().length) {
        alert("내용은 4글자 이상, 2000글자 미만입니다.");
        content.focus();
        return true;
    }

    return false;
}
