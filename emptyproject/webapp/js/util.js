$(function(){
	// 处理所有的多选标签
	$(".selectAll").each(function(index,item){
		$(item).click(function(){
			$(":checkbox[name='"+this.id+"']").attr("checked",this.checked);
		});
	});
});