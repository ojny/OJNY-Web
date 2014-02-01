$("#problem-info").submit ->
  postData = $("#problem-info").serialize()
  $.ajax
    url: "/admin/problem/add"
    data: postData
    type: "post"
    dataType: "json"
    success: (ret) ->
      console.log ret
      if ret["error"] == 0
        window.location = "/admin/problems"
      else
        alert ret["message"]

  return false