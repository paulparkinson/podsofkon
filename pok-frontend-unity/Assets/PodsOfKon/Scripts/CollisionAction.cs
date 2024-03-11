using UnityEngine;

public class CollisionAction : MonoBehaviour
{
    public GameObject objectHitSound; ////
    public GameObject objectHitSoundPlayer2; ////
    public GameObject objectFlyingSound;
    public GameObject capturedTargetToActivatePlayer1;
    public GameObject capturedTargetToActivatePlayer2;
    private Animator animator;
    private static string _provision = "provision";
    private static string _answerquestion = "answerquestion";
    private static string _submitanswer = "submitanswer";
    private static string _derezz = "derezz";
    private static string _wall = "wall";
    private static string _player1block = "player1block";
    private static string _player2block = "player2block";
    public string action =_provision;
    public string objectName ="test";
    public int answerIndexNumber = -1;
    public  BonusRoundQuestions bonusRoundQuestions;
    public bool isStill ; // means it is provisioned, ie not active target for player, only for opponent
    void Start()
    {
        if (!isStill && objectFlyingSound!= null)
        {
            objectFlyingSound.GetComponent<AudioSource>().Play();
        }
        animator = GetComponent<Animator>();     
        // Debug.Log("animator:" + animator );
    }

    public string DoAction(GameObject otherGameObject, bool isPlayer1)
    {
   //     Debug.Log("CollisionDoAction this GameObject:" + gameObject + " otherGameObjectotherGameObject:" + otherGameObject);
        if (action.Equals(_provision))
        {
            PlayAudioSprite playAudioSprite = isPlayer1
                ? objectHitSound.GetComponent<PlayAudioSprite>()
                    : objectHitSoundPlayer2.GetComponent<PlayAudioSprite>();
            if (playAudioSprite != null)
            {
                playAudioSprite.Play(objectName);
            }
            else    objectHitSound.GetComponent<AudioSource>().Play();
          //  var playerName = GetPlayerName();
            ScoreKeeper.getInstance().Provision(isPlayer1 ? "player1" : "player2", objectName);
            
            PlayAudioSprite playAudioSprint =  objectFlyingSound.GetComponent<PlayAudioSprite>();
             objectFlyingSound.GetComponent<AudioSource>().Stop();
            //if we wanted to instead turn off the animator and move the object, we'd do this...
            // if(GetComponent<Animator>() !=null) GetComponent<Animator>().enabled = false;
            // transform.position = new Vector3(whereToPlaceCapturedTarget.transform.position.x , whereToPlaceCapturedTarget.transform.position.y, whereToPlaceCapturedTarget.transform.position.z);
            //but we'll leave the animation, remove the target object and set the capture object that corresponds to it to active
      //      Debug.Log("CollisionDoAction this GameObject:" + gameObject + " otherGameObjectotherGameObject:" + otherGameObject + " capturedTargetToActivate:" + capturedTargetToActivate);
            if (isPlayer1) capturedTargetToActivatePlayer1.SetActive(true); 
            else capturedTargetToActivatePlayer2.SetActive(true);
    //        Debug.Log("CollisionDoAction this GameObject:" + gameObject + " otherGameObjectotherGameObject:" + otherGameObject + " this gameObject to deactivate:" + gameObject);
            gameObject.SetActive(false);
            //Destroy(this); //todo verify this
            TargetController.getSingleton().SpawnNextRandom();
        } else if(action.Equals(_derezz)) { 
            PlayAudioSprite playAudioSprite = objectHitSound.GetComponent<PlayAudioSprite>();
            if(playAudioSprite != null) playAudioSprite.Play(objectName);
            else    objectHitSound.GetComponent<AudioSource>().Play();
            // var playerName = GetPlayerName();
            ScoreKeeper.getInstance().Derezz(isPlayer1 ? "player1" : "player2", objectName);
            gameObject.SetActive(false);
        } else if(action.Equals(_wall)) { 
            objectHitSound.GetComponent<AudioSource>().Play();
        } else if(action.Equals(_player1block)) { 
            objectHitSound.GetComponent<AudioSource>().Play();
            animator.Play("Block" );
        } else if(action.Equals(_player2block)) { 
            objectHitSound.GetComponent<AudioSource>().Play();
            animator.Play("Block" );
        } else if(action.Equals(_answerquestion)) { //bonus round/scene
            objectHitSound.GetComponent<AudioSource>().Play();
            bonusRoundQuestions.ProcessAnswerSelection(answerIndexNumber, gameObject);
        } else if(action.Equals(_submitanswer)) { 
            bonusRoundQuestions.ProcessSubmit();
        }
        return "actiondone";
}



}