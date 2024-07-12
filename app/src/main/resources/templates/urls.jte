@import java.util.List
@import hexlet.code.model.Url
@param List<Url> urls

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Список URL</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-4">
    <h1>Список URL</h1>
    <ul class="list-group">
        @for(Url url : urls)
            <li class="list-group-item">
                <a href="/urls/${url.getId()}">${url.getName()}</a>
            </li>
        @endfor
    </ul>
    <a href="/" class="btn btn-secondary mt-4">Назад</a>
</div>
</body>
</html>
