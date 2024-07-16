@import java.util.List
@import hexlet.code.model.UrlCheck
@param List<UrlCheck> checks

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

    <table class="table">
        <thead>
        <tr>
            <th scope="col">#</th>
            <th scope="col">Дата проверки</th>
            <th scope="col">Код статуса</th>
            <th scope="col">Title</th>
            <th scope="col">H1</th>
            <th scope="col">Description</th>
        </tr>
        </thead>
        <tbody>
        @for (UrlCheck check : checks) {
        <tr>
            <th scope="row">@check.getId()</th>
            <td>@check.getCreatedAt()</td>
            <td>@check.getStatusCode()</td>
            <td>@check.getTitle()</td>
            <td>@check.getH1()</td>
            <td>@check.getDescription()</td>
        </tr>
        }
        @endfor
        </tbody>
    </table>
    <a href="/" class="btn btn-secondary mt-4">Назад</a>
</div>
</body>
</html>
