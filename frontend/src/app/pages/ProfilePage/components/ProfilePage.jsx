import React from 'react';
import DatePicker from 'react-datepicker';
import moment from 'moment';
import 'react-datepicker/dist/react-datepicker.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import './style.css'
import PropTypes from 'prop-types';

	/**
   *  Profile Page component. Contains the profile form.
   */
class ProfilePage extends React.Component {
	static propTypes = {
        /** Current user */
        profileinfo: PropTypes.object
    }
	constructor(props) {
		super(props);
		this.state = {
			username: '',
			fullname: '',
			mailuser: '',
			imagePreviewUrl: props.profileinfo.photo,
			birthday : props.profileinfo.birthday,
			day:'',
			month:'',
			year:'',
		}

		this.handleDateChange = this.handleDateChange.bind(this);
		this.handleChange = this.handleChange.bind(this);
		this.handleSubmit = this.handleSubmit.bind(this);
		this.handleClear = this.handleClear.bind(this);
		this.handleYearChange = this.handleYearChange.bind(this);
		this.handleMonthChange = this.handleMonthChange.bind(this);
		this.handleDayChange = this.handleDayChange.bind(this);
	}

	/**
    * Updates year property of birthday
    *
    * @param {event} event
    * @public
    */
	handleYearChange(event){event.preventDefault();this.setState({year: event.target.value});};

	/**
    * Updates month property of birthday
    *
    * @param {event} event
    * @public
    */
	handleMonthChange(event){event.preventDefault();  this.setState({month: event.target.value});};

	/**
    * Updates day property of birthday
    *
    * @param {event} event
    * @public
    */
	handleDayChange(event){event.preventDefault();  this.setState({day: event.target.value});};

	/**
    * Clears inputs entered
    *
    * @param {event} e
    * @public
    */
	handleClear(e) { e.preventDefault();window.location.replace("/profile");};

	/**
    * Updates date property of birthday
    *
    * @param {string} date
    * @public
    */
	handleDateChange(date){this.setState({birthday: date});};

	/**
    * Updates username, full name and e-mail properties
    *
    * @param {event} event
    * @public
    */
	handleChange(event) {
		event.preventDefault();
		if(event.target.id == "username"){
			this.setState({username: event.target.value});
		} else if(event.target.id == "fullname"){
			this.setState({fullname: event.target.value});
		} else {
			this.setState({mailuser: event.target.value});
		}
	}

	/**
    * Creates JSON object from input and sends it via API
    *
    * @param {event} e
    * @public
    */
	handleSubmit(e){
		e.preventDefault();

		var myHeaders = new Headers();
		var birthday_year = this.state.year;
		var nameFull = this.state.fullname;
		var mail = this.state.mailuser;
		var str = "";
		var valueBirthday = this.props.profileinfo.birthday;
		if(birthday_year.localeCompare(str) != 0){
			valueBirthday = this.state.year + '-' + this.state.month + '-' + this.state.day;
		}
		if(nameFull.localeCompare(str) == 0){
			nameFull = this.props.profileinfo.fullName;
		}
		if(mail.localeCompare(str) == 0){
			mail = this.props.profileinfo.email;
		}
		var payload = {
			"birthday": valueBirthday,
			"email" : mail,
			"photo" : this.state.imagePreviewUrl,
			"fullName": nameFull,
			"location": this.props.profileinfo.location,
		};

		var token = getCookie('token');
		fetch('http://52.90.34.144:85/api/profile',
		{

		headers: {
			'Accept': 'application/json',
			'Content-Type': 'application/json',
			'Authorization': 'JWT ' + token
		},

			method: 'POST',
			body: JSON.stringify( payload )
		})

		.then(function(res){
			if(res.ok){
				window.location.replace("/Profile");
			} else {
				alert("Couldn't upload");
			}
		})
		.catch((error) => {
			console.error(error);
		})
	};

	/**
    * Updates profile image
    *
    * @param {event} e
    * @public
    */

	_handleImageChange(e) {
		e.preventDefault();

		let reader = new FileReader();
		let file = e.target.files[0];

		reader.onloadend = () => {
			this.setState({
				file: file,
				imagePreviewUrl: reader.result
			});
		}

		reader.readAsDataURL(file)
	}

	render() {

		let vyear,vmonth,vday = null;
		if(this.props.profileinfo.birthday){
			var res = this.props.profileinfo.birthday.split("-");
			vyear = res[0];
			vmonth = res[1];
			vday = res[2];
		}else{
			vyear = 'YYYY';
			vmonth = 'MM';
			vday = 'DD'
		}

		let {imagePreviewUrl} = this.state;
		let $imagePreview = null;
		if (imagePreviewUrl) {
			var url = imagePreviewUrl;
			$imagePreview = (<img src = {url} className = "avatar img-circle" alt ="avatar"  width="200" height="200"/>);
		} else if(this.props.profileinfo.photo){
			var url = '//' + this.props.profileinfo.photo;
			$imagePreview = (<img src = {url} className = "avatar img-circle" alt ="avatar"  width="200" height="200"/>);
		}else{
			$imagePreview = (<img src="//placehold.it/100" className = "avatar img-circle" alt ="avatar"  width="200" height="200"/>);
		}

		return(
		<div className="container-fluid profile-body">
			<div className="row profile-container">
			<h1>Edit Profile</h1>
	  	<hr/>


				<div className="col-md-4">
					<div className="text-center">
						 {$imagePreview}
						<h6>Upload a different photo...</h6>

						<form onSubmit={(e)=>this._handleSubmit(e)}>
							<input className="form-control"
								type="file"
								onChange={(e)=>this._handleImageChange(e)} />
						</form>

					</div>
				</div>

				<div className="col-md-8 personal-info">

				<h3>Personal info</h3>
				<form className="form-horizontal" role="form">

					<div className="form-group">
						<label className="col-lg-3 control-label ">Username:</label>
						<div className="col-lg-8">
							<input className="form-control"
								type="text" id="username" onChange={this.handleChange} placeholder={this.props.profileinfo.username}
								  />
						</div>
					</div>

					<div className="form-group">
						<label className="col-lg-3 control-label">Full Name:</label>
						<div className="col-lg-8">
							<input className="form-control"
								type="text"
								placeholder={this.props.profileinfo.fullName}
								id="fullname"
								name="title"
								ref="title"
								onChange={ this.handleChange }
									/>
						</div>
					</div>

					<div className="form-group">
						<label className="col-lg-3 control-label">Email:</label>
						<div className="col-lg-8">
							<input className="form-control"
								type="text"
								placeholder={this.props.profileinfo.email}
								id="email"
								name="title"
								ref="title"
								onChange={ this.handleChange }
									/>
						</div>
					</div>

					<div className="form-group">
						<label className="col-lg-3 control-label">Birthday:</label>
						<div className="col-lg-2">
							<input className="form-control"
								type="number"
								name="day"
								ref="day"
								min="1"
								max="31"
								placeholder={vday}
								value={ this.state.subject }
								onChange={ this.handleDayChange }
							required />
						</div>
						<div className="col-lg-2">
							<input className="form-control"
								type="number"
								name="month"
								ref="month"
								min="1"
								max="12"
								placeholder={vmonth}
								value={ this.state.subject }
								onChange={ this.handleMonthChange }
							required />
						</div>
						<div className="col-lg-2">
							<input className="form-control"
								type="number"
								name="year"
								ref="year"
								min="0"
								max="3000"
								placeholder={vyear}
								value={ this.state.subject }
								onChange={ this.handleYearChange }
							required />
						</div>
					</div>

					<div class="form-group">
						<label class="col-md-3 control-label"></label>
						<div class="col-md-8 classWithPad">
							<button className="btn btn-primary custom"
								onClick={ this.handleSubmit }>Submit</button>
							<span></span>
							<button className="btn btn-primary custom margin-left"
								onClick={ this.handleClear }>Clear</button>
						</div>
					</div>
					</form>

				</div>
			</div>
		</div>
		);
	}
};

function getCookie(key) {
    var keyValue = document.cookie.match('(^|;) ?' + key + '=([^;]*)(;|$)');
    return keyValue ? keyValue[2] : null;
}
export default ProfilePage
