import React from 'react';
import TagsInput from 'react-tagsinput'
import 'react-tagsinput/react-tagsinput.css'

class ContactForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            file: '',
            imagePreviewUrl: ' ',
            name: '',
            description: '',
            location: '',
            date:'2017-01-01',
            image: '',
            tags: [],
            tag: '',
        }
        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handleNameChange = this.handleNameChange.bind(this);
        this.handleDescriptionChange = this.handleDescriptionChange.bind(this);
        this.handleTagsChange = this.handleTagsChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleClear = this.handleClear.bind(this);
        this.handleChangeTaags = this.handleChangeTaags.bind(this);
    }

    handleChangeTaags(tags) {this.setState({tags});}

    handleUsernameChange(event){event.preventDefault();this.setState({username: event.target.value});};


    _handleSubmit(e) {
      e.preventDefault();
      // TODO: do something with -> this.state.file
      console.log('handle uploading-', this.state.imagePreviewUrl);
    }

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


    handleClear(e) { e.preventDefault();window.location.replace("/upload");}

    // onChange handler saves subject to state
    handleNameChange(event){event.preventDefault();this.setState({name: event.target.value});};

    // onChange handler saves subject to state
    handleDescriptionChange(event){event.preventDefault();this.setState({description: event.target.value});};
    handleLocationChange(event){event.preventDefault();this.setState({location: event.target.value});};
    handleDateChange(event){event.preventDefault();this.setState({date: event.target.value});};
    handleImageChange(event){event.preventDefault();this.setState({image: event.target.value});};
    handleTagsChange(event){event.preventDefault();this.setState({tags: event.target.value});};

    handleChangeTags(event, tags) {event.preventDefault();this.setState({tags});};
    handleChangeInput(event, tag) {event.preventDefault();this.setState({tag});};



    // Handler for the button/submit event
    handleSubmit(e){
      e.preventDefault();

      var myHeaders = new Headers();

      var payload = {
        "name" : this.state.name,
        "description": this.state.description,
        "location": this.state.location,
        "date": this.state.date,
        "image": this.state.imagePreviewUrl,
        "tags": this.state.tags
      };

      //console.log(data);
      var token = getCookie('token');
      fetch('http://52.90.34.144:85/api/items/',
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
          window.location.replace("/");
        } else {
          alert("Couldn't upload");
        }
      })
      .catch((error) => {
        console.error(error);
      })
    };

    render() {

      let {imagePreviewUrl} = this.state;
      let $imagePreview = null;
      if (imagePreviewUrl) {
        $imagePreview = (<img src={imagePreviewUrl} />);
      } else {
        $imagePreview = (<div className="previewText">Please select an Image for Preview</div>);
      }

        return (
          <div className="row">
            <div className="col-md-6" >
              <div className="previewComponent">
                <h1 align = "center"> Upload New Item</h1>
                <form onSubmit={(e)=>this._handleSubmit(e)}>
                  <input className="fileInput"
                    type="file"
                    onChange={(e)=>this._handleImageChange(e)} />
                </form>
                <div className="imgPreview">
                  {$imagePreview}
                </div>
              </div>
            </div>


            <div className="col-md-6 ">
                <form>
                {/*<div className="form-group">
                  <label id="username">Username:</label>
                  <input type="text" id="username" onChange={this.handleUsernameChange} placeholder={this.state.profileinfo.username}/>
                  <input className="form-control"
                    type="text"
                    name="username"
                    ref="username"
                    placeholder = {this.props.username}
                    value={ this.state.subject}
                    onChange={ this.handleUsernameChange}
                  required />
                </div>*/}
                  <div className="form-group">
                    <label id="titleID">Title</label>
                    <input className="form-control"
                      type="text"
                      name="title"
                      ref="title"
                      value={ this.state.subject }
                      onChange={ this.handleNameChange }
                    required />
                  </div>
                  <div className="form-group">
                      <label id="descID">Description</label>
                      <textArea className="form-control"
                        rows="8"
                        type="text"
                        name="description"
                        ref="description"
                        value={ this.state.subject }
                        onChange={ this.handleDescriptionChange }
                        required />
                  </div>
                  <div className="form-group">
                    <label id="tagsID">Tags</label>
                    <TagsInput value={this.state.tags} onChange={this.handleChangeTaags} />
                    <br/>
                  </div>
                  <button className="btn btn-primary custom"
                    onClick={ this.handleSubmit }>Submit</button>
                  <button className="btn btn-primary custom margin-left"
                    onClick={ this.handleClear }>Clear</button>
                </form>
            </div>
          </div>
        );
    }
}

function getCookie(key) {
    var keyValue = document.cookie.match('(^|;) ?' + key + '=([^;]*)(;|$)');
    return keyValue ? keyValue[2] : null;
}
export default ContactForm;
