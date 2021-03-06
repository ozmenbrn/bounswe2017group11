import React from 'react';

class Comment extends React.Component {
    constructor(props){
      super(props);
   }

   render() {
   let comment;
   if(typeof this.props.item.created_by != "undefined") {
    comment = <div>
        <div class="mycontainer2">
        <div class="panel panel-white post">
            <div class="card-header text-white bg-info">
                Item details
                </div>
            <div class="card-body">
            <h5 class="card-title">Id: {this.props.item.id}</h5>
            <h5 class="card-title">Created by: {this.props.item.created_by.username}</h5>

            <h5 class="card-title">Created at: {this.props.item.created_at}</h5>

            </div>
        </div>
         
        <br />

        <div class="panel panel-white post">
            <div class="card-header text-white bg-info">
                Comments
            </div>
            <div class="post-footer">
                <div class="input-group"> 
                    <input class="form-control" placeholder="Add a comment" type="text"/>
                    <span class="input-group-addon">
                        <a href="#"><i class="fa fa-edit"></i></a>  
                    </span>
                </div>
                <ul class="comments-list">
                    <li class="comment">
                        <a class="pull-left" href="#">
                            <img class="avatar" src="http://bootdey.com/img/Content/user_1.jpg" alt="avatar"/>
                        </a>
                        <div class="comment-body">
                            <div class="comment-heading">
                                <h4 class="user">Canberk</h4>
                                <h5 class="time">5 minutes ago</h5>
                            </div>
                            <p>Wow! Hilarious. </p>
                        </div>
                    </li>
                </ul>
        </div>
    </div>
</div>

    </div> 
} else{
    comment = null;
}
      return (
      <div>
            {comment}
        </div>
      );
   }
}

export default Comment;

