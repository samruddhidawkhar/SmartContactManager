console.log("This is script file")

const toggleSidebar = () => {

    if($(".sidebar").is(":visible")){
       
        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%")
    }
    else{

        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%")

    }
};

const search=()=>{
	
	let query = $("#search-input").val();
	
	if(query==''){
		
		$(".search-result").hide();
	}else{
		console.log(query);
		
		let url= `http://localhost:9092/search/${query}`;
		
		fetch(url)
		 .then((response) => {
			 return response.json();
		 })
		 .then((data) => {
			console.log(data); 
			
			let text= `<div class='list-group'>`;
			
			data.forEach((contact) => {
				text += `<a href='/user/${contact.cid}/contact/' class='list-group-item list-group-item-action'>${contact.name}</a>`
			});
			
			text += `</div>`;
			$(".search-result").html(text);
			$(".search-result").show();
		 });
		
	    }
};

//first request - to server to create order

const paymentStart = () => {
	console.log("payment started...");
	let amount = $("#payment_field").val()
	console.log(amount);
	
	if(amount == "" || amount == null){
	//	alert("amount is required !!");
		swal("Failed!", "amount is required !!", "error");
		return;
	}
	
	//code...
	//we will use ajax to send request to server to create order jquery
	$.ajax(
		
		{
			url:'/user/create_order',
			data:JSON.stringify({amount:amount,info:'order_request'}),
			contentType:'application/json',
			type:'POST',
			dataType:'json',
			success:function(response){
				//invoke when success
				console.log(response)
				if(response.status == "created"){
					//open payment form
					let options = {
						key: "rzp_test_QACBg8MrqlVJFn",
						amount: response.amount,
						currency: "INR",
						name: "Smart Contact Manager",
						description: "Donation",
						order_id: response.id,
						
						handler:function(response){
							console.log(response.razorpay_payment_id)
							console.log(response.razorpay_order_id)
							console.log(response.razorpay_signature)
							console.log('payment successful !! ')
							//alert("congrates !! Payment successful !!");
							
							updatePaymentOnServer(
								response.razorpay_payment_id, 
								response.razorpay_order_id, 
								'paid');
							swal("Good job!", "congrates !! Payment successful !!", "success");
						},
						 "prefill": { //We recommend using the prefill parameter to auto-fill customer's contact information especially their phone number
					        "name": "", //your customer's name
					        "email": "",
					        "contact": "" //Provide the customer's phone number for better conversion rates 
					    },
					      "notes": {
					        "address": "Smart Contact Manager (SCM)"
					    },
					    "theme": {
					        "color": "#3399cc"
					    }
					};
					
					let rzp = new Razorpay(options);
					
					rzp.on('payment failed', function(response){
						console.log(response.error.code);
						console.log(response.error.description);
						alert("OOPS payment failed !!")
						swal("Failed!", "OOPS payment failed !!", "error");
					});
					
					rzp.open();
				}
			},
			error:function(error){
				//invoke when error
				console.log(error)
				alert("Something went wrong !!")
			}
		}
	)
	
};


function updatePaymentOnServer(payment_id,order_id,status)
{
	$.ajax({
		url:'/user/update_order',
			data:JSON.stringify({
				payment_id: payment_id,
				order_id: order_id,
				status: status,
				}),
			contentType:'application/json',
			type:'POST',
			dataType:'json',
			success:function(response){
				swal("Good job!", "congrates !! Payment successful !!", "success");
			},
			error:function(error){
				swal("Failed!", 
				"Your payment is successful, but we did not get on server, we will contact you as soon as possible", 
				"error");
			},
	})
}






