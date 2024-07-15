@import hexlet.code.model.Url
@import java.util.List
@import java.util.Map
@param Url url
@param List<Map<String, String>> checks

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Детали URL</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-4">
    <h1>Детали URL</h1>
    <p>ID: ${url.getId()}</p>
    <p>URL: ${url.getName()}</p>

    <!-- Форма для создания новой проверки -->
    <form action="/urls/${url.getId()}/checks" method="post">
        <button type="submit" class="btn btn-primary">Проверить URL</button>
    </form>

    <h2 class="mt-4">Проверки</h2>
    <ul class="list-group">
        @for(Map<String, String> check : checks)
            <li class="list-group-item">
                <p>ID: ${check.get("id")}</p>
                <p>Статус: ${check.get("statusCode")}</p>
                <p>H1: ${check.get("h1")}</p>
                <p>Title: ${check.get("title")}</p>
                <p>Description: ${check.get("description")}</p>
                <p>Дата проверки: ${check.get("createdAt")}</p>
            </li>
        @endfor
    </ul>

    <a href="/urls" class="btn btn-secondary mt-4">Назад</a>
</div>
</body>
</html>
